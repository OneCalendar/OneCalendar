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
import org.scalatest.FunSuite
import models.Event

class EventDaoTest extends FunSuite with ShouldMatchers with DaoCleaner {

    /**
     * RUNNING MONGO SERVER BEFORE -
     * mongod --dbpath data/ --fork --logpath data/mongodb.log
     */

    val eventDevoxx: Event = Event(
            uid = "1",
            title = "BOF",
            begin = new DateTime(2012, 04, 20, 0, 0, 0, 0),
            end = new DateTime(2012, 04, 20, 0, 0, 0, 0),
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

    val oldEvent : Event = Event(
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
        initData

        EventDao.findByTag(List("devoxx")) should be(List(eventDevoxx))
    }

    test("should find events by tags 'devoxx' or 'java' ") {
        initData

        EventDao.findByTag(List("devoxx", "java")) should be(List(eventDevoxx, eventJava))
    }

    test("should find 3 first events by tags 'devoxx', 'java' or other ") {
        implicit val now : () => Long = () => new DateTime(2010,1,1,1,1).getMillis
        initFourData

        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should have size 3
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should be(List(eventJava, eventDevoxx, eventOther))
    }

    test("should not return past events") {
        implicit val now : () => Long = () => new DateTime(2012, 4, 20, 0, 0, 0, 0).getMillis
        initFourData

        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should have size 2
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).previewEvents should be(List(eventOther, event4))
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

    test("should not list old tags") {
        implicit val now : () => Long = () => new DateTime(2012,5,1,1,1).getMillis

        EventDao.saveEvent(oldEvent)
        EventDao.saveEvent(newEvent)

        val tags: List[String] = EventDao.listTags()
        tags should be(List("NEW"))
    }

    test("delete by originalStream don't drop the older event or event without relation") {
        implicit val now : () => Long = () => DateTime.now.getMillis
        EventDao.saveEvent(Event(
            originalStream = "hello",
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1","tag2")
        ))
        EventDao.saveEvent(Event(
            originalStream = "hello",
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title2",
            description = "description2",
            tags = List("tag1","tag2")
        ))
        EventDao.saveEvent(Event(
            originalStream = "hello",
            begin = new DateTime().minusDays(10),
            end = new DateTime().minusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1","tag2")
        ))
        initData

        EventDao.findAll() should have size 5

        EventDao.deleteByOriginalStream("hello")

        EventDao.findAll() should have size 3
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