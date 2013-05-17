/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dao

import org.joda.time.DateTime
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import models.Event
import dao.EventRepository._
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}

class EventDaoTest extends FunSuite with ShouldMatchers with MongoEmbedDatabase with BeforeAndAfterAll with DaoCleaner {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27017) }
    override def afterAll() { mongoStop(mongoProps) }

    test("saving a new event") {
        val event: Event = Event(
            uid = "1",
            title = "BOF",
            begin = new DateTime(2012, 04, 19, 0, 0, 0, 0),
            end = new DateTime(2012, 04, 19, 0, 0, 0, 0),
            description = "",
            location = "",
            tags = List("JAVA", "DEVOXX")
        )

        EventDao.saveEvent(event)
        EventDao.findAll should be(List(event))
    }

    test("should find event by tag 'devoxx'") {
        initData()
        EventDao.findByTag(List("devoxx")) should be(List(eventDevoxx))
    }

    test("should find events by tags 'devoxx' or 'java' ") {
        initData()
        EventDao.findByTag(List("devoxx", "java")).map(_.tags).flatten.sorted should be(List("JAVA", "DEVOXX").sorted)
    }

    test("should find event even if it have not originalStream and url") {
        val eventWithNoOrigStreamAndUrl =
            Event("uid", "title", DateTime.now().plusDays(1), DateTime.now().plusDays(2), "location", "description", tags = List("TEST"))

        EventDao.saveEvent(eventWithNoOrigStreamAndUrl)

        EventDao.findByTag(List("test")) should be(List(eventWithNoOrigStreamAndUrl))
        EventDao.findAll() should be(List(eventWithNoOrigStreamAndUrl))
    }

    test("should not find event without uid but which is in database") {
        val now: DateTime = DateTime.now

        EventDao.saveEvent(Event(uid = null, tags = List("NO_UID"), begin = now, end = now))

        EventDao.findByTag(List("NO_UID")) should be(List(Event(uid = "", tags = List("NO_UID"), begin = now, end = now)))
    }

    test("should find 3 first events by tags 'devoxx', 'java' or other ") {
        implicit val now: () => Long = () => new DateTime(2010, 1, 1, 1, 1).getMillis

        initFourData()

        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should have size 3
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should be(List(eventJava, eventDevoxx, eventOther))
    }

    test("should not return past events") {
        implicit val now: () => Long = () => new DateTime(2012, 4, 20, 0, 0, 0, 0).getMillis

        initFourData()

        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should have size 3
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents.map(_.begin.getMillis).foreach(_ should be >= (now()))
    }

    test("should find everything") {
        (1 to 50).foreach(
            id => EventDao.saveEvent(
                Event(
                    uid = id.toString,
                    title = id.toString,
                    begin = new DateTime,
                    end = new DateTime,
                    tags = Nil
                )
            )
        )

        EventDao.findAll should have size 50
    }

    test("should find all events from now") {
        implicit val now = () => DateTime.now.getMillis

        EventDao.saveEvent(oldEvent)
        EventDao.saveEvent(newEvent)

        EventDao.findAllFromNow() should be(List(newEvent))
    }

    test("should not list old tags") {
        implicit val now: () => Long = () => new DateTime(2012, 5, 1, 1, 1).getMillis

        EventDao.saveEvent(oldEvent)
        EventDao.saveEvent(newEvent)

        val tags: List[String] = EventDao.listTags()
        tags should be(List("NEW"))
    }

    test("delete by originalStream will drop all") {
        implicit val now: () => Long = () => DateTime.now.getMillis

        EventDao.saveEvent(Event(
            originalStream = Option("hello"),
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1", "tag2")
        ))
        EventDao.saveEvent(Event(
            originalStream = Option("hello"),
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title2",
            description = "description2",
            tags = List("tag1", "tag2")
        ))
        EventDao.saveEvent(Event(
            originalStream = Option("hello"),
            begin = new DateTime().minusDays(10),
            end = new DateTime().minusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1", "tag2")
        ))
        initData()

        EventDao.findAll() should have size 5

        EventDao.deleteByOriginalStream("hello")

        EventDao.findAll() should have size 2
    }

    test("current events or next ones") {
        import scala.concurrent.duration._
        implicit val now: () => Long = () => new DateTime(2012, 4, 21, 15, 00).getMillis


        initFourData()

        val closestEvents: List[Event] = EventDao.closestEvents(offset = 5, afterset = 2)
        closestEvents should have size 2
        closestEvents.map(_.begin.getMillis).foreach(_.should(be <= (now() + (2 hours).toMillis) or be >= (now() - (5 minutes).toMillis)))
    }

    test("current events or next ones with tag 'devoxx'") {
        import scala.concurrent.duration._
        implicit val now: () => Long = () => new DateTime(2012, 4, 20, 10, 00).getMillis


        initFourData()
        val closestEvents: List[Event] = EventDao.closestEvents(offset = 5, afterset = 2, tags = List("devoxx"))
        closestEvents should have size 1
        closestEvents.map(_.begin.getMillis).foreach(_.should(be <= (now() + (2 hours).toMillis) or be >= (now() - (5 minutes).toMillis)))
    }

    test("count futur events") {
        implicit val now: () => Long = () => new DateTime(2012, 5, 1, 1, 1).getMillis

        EventDao.saveEvent(oldEvent)
        EventDao.saveEvent(newEvent)

        EventDao.countFutureEvents should be(1)
    }

    private def initData() {
        EventDao.saveEvent(eventDevoxx)
        EventDao.saveEvent(eventJava)
    }

    private def initFourData() {
        EventDao.saveEvent(eventDevoxx)
        EventDao.saveEvent(eventJava)
        EventDao.saveEvent(eventOther)
        EventDao.saveEvent(event4)
    }
}

object EventRepository {
    val eventDevoxx: Event = Event(
        uid = "1",
        title = "BOF",
        begin = new DateTime(2012, 04, 20, 10, 0, 0, 0),
        end = new DateTime(2012, 04, 20, 11, 0, 0, 0),
        tags = List("DEVOXX")
    )

    val eventJava: Event = Event(
        uid = "2",
        title = "BOF",
        begin = new DateTime(2012, 04, 19, 10, 0, 0, 0),
        end = new DateTime(2012, 04, 19, 11, 0, 0, 0),
        tags = List("JAVA")
    )

    val eventOther: Event = Event(
        uid = "3",
        title = "BOF",
        begin = new DateTime(2012, 04, 21, 15, 0, 0, 0),
        end = new DateTime(2012, 04, 21, 16, 0, 0, 0),
        tags = List("OTHER")
    )

    val event4: Event = Event(
        uid = "4",
        title = "BOF",
        begin = new DateTime(2012, 04, 21, 15, 0, 0, 0),
        end = new DateTime(2012, 04, 21, 16, 0, 0, 0),
        tags = List("4", "OTHER")
    )

    val oldEvent: Event = Event(
        uid = "4",
        title = "BOF",
        begin = new DateTime(2012, 04, 21, 15, 0, 0, 0),
        end = new DateTime(2012, 04, 21, 16, 0, 0, 0),
        tags = List("4", "OTHER")
    )

    val newEvent: Event = Event(
        uid = "NEW",
        title = "NEW",
        begin = new DateTime().plusDays(10),
        end = new DateTime().plusDays(10),
        tags = List("NEW")
    )
}