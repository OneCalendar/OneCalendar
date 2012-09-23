/*
 * This file is part of OneCalendar.
 *
 * OneCalendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OneCalendar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
 */

package dao

import configuration.injection.MongoConfiguration
import models.Event
import org.joda.time.DateTime
import collection.immutable.List
import com.mongodb._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSuite}
import models.builder.EventBuilder

class EventDaoTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    /**
     * RUNNING MONGO SERVER BEFORE -
     * mongod --dbpath data/ --fork --logpath data/mongodb.log
     */

    implicit val mongoConfigurationTesting = MongoConfiguration("test")

    val eventDevoxx: Event = new EventBuilder()
        .uid("1")
        .title("BOF")
        .begin(new DateTime(2012, 04, 20, 0, 0, 0, 0))
        .end(new DateTime(2012, 04, 20, 0, 0, 0, 0))
        .tags(List("DEVOXX"))
        .toEvent

    val eventJava: Event = new EventBuilder()
        .uid("2")
        .title("BOF")
        .begin(new DateTime(2012, 04, 19, 10, 0, 0, 0))
        .end(new DateTime(2012, 04, 19, 11, 0, 0, 0))
        .tags(List("JAVA"))
        .toEvent

    val eventOther: Event = new EventBuilder()
        .uid("3")
        .title("BOF")
        .begin(new DateTime(2012, 04, 21, 15, 0, 0, 0))
        .end(new DateTime(2012, 04, 21, 16, 0, 0, 0))
        .tags(List("OTHER"))
        .toEvent

    val event4: Event = new EventBuilder()
        .uid("4")
        .title("BOF")
        .begin(new DateTime(2012, 04, 21, 15, 0, 0, 0))
        .end(new DateTime(2012, 04, 21, 16, 0, 0, 0))
        .tags(List("4", "OTHER"))
        .toEvent

    val oldEvent : Event = new EventBuilder()
        .uid("4")
        .title("BOF")
        .begin(new DateTime(2012, 04, 21, 15, 0, 0, 0))
        .end(new DateTime(2012, 04, 21, 16, 0, 0, 0))
        .tags(List("4", "OTHER"))
        .toEvent

    val newEvent: Event = new EventBuilder()
        .uid("NEW")
        .title("NEW")
        .begin(new DateTime().plusDays(10))
        .end(new DateTime().plusDays(10))
        .tags(List("NEW"))
        .toEvent

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB("test")
        db
    }

    before {
        db.requestStart
        db.getCollection("events").drop
        mongoConfigurationTesting.now = new DateTime().getMillis
    }

    after {
        db.requestDone
    }

    test("saving a new event") {
        val event: Event = new EventBuilder()
            .uid("1")
            .title("BOF")
            .begin(new DateTime(2012, 04, 19, 0, 0, 0, 0))
            .end(new DateTime(2012, 04, 19, 0, 0, 0, 0))
            .description("")
            .location("")
            .tags(List("JAVA", "DEVOXX"))
            .toEvent

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
        initFourData
        mongoConfigurationTesting.now = new DateTime(2012, 1, 1, 0, 0, 0, 0).toDate.getTime
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).events should have size 3
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).events should be(List(eventJava, eventDevoxx, eventOther))
    }

    test("should not return past events") {
        initFourData
        mongoConfigurationTesting.now = new DateTime(2012, 4, 20, 0, 0, 0, 0).toDate.getTime
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).events should have size 2
        EventDao.findPreviewByTag(List("devoxx", "java", "other")).events should be(List(eventOther, event4))

    }


    test("should find everything") {
        (1 to 50).foreach(
            id => EventDao.saveEvent(
                new EventBuilder()
                    .uid(id.toString)
                    .title(id.toString)
                    .begin(new DateTime)
                    .end(new DateTime)
                    .tags(List())
                    .toEvent
            )
        )
        EventDao.findAll should have size 50
    }

    test("should not list old tags") {
        EventDao.saveEvent(oldEvent)
        EventDao.saveEvent(newEvent)
        val tags: List[String] = EventDao.listTags()
        tags should be(List("NEW"))
    }

    test("delete by originalStream don't drop the older event or event without relation") {
        mongoConfigurationTesting.now = new DateTime().getMillis
        EventDao.saveEvent(new EventBuilder()
            .originalStream("hello")
            .begin(new DateTime().plusDays(10))
            .end(new DateTime().plusDays(10))
            .title("title")
            .description("description")
            .tags(List("tag1","tag2"))
            .toEvent)
        EventDao.saveEvent(new EventBuilder()
            .originalStream("hello")
            .begin(new DateTime().plusDays(10))
            .end(new DateTime().plusDays(10))
            .title("title2")
            .description("description2")
            .tags(List("tag1","tag2"))
            .toEvent)
        EventDao.saveEvent(new EventBuilder()
            .originalStream("hello")
            .begin(new DateTime().minusDays(10))
            .end(new DateTime().minusDays(10))
            .title("title")
            .description("description")
            .tags(List("tag1","tag2"))
            .toEvent)
        initData

        EventDao.findAll() should have size 5

        EventDao.deleteByOriginalStream("hello")

        EventDao.findAll() should have size 3


    }

    private def initData {
        EventDao.saveEvent(eventDevoxx)
        EventDao.saveEvent(eventJava)
    }

    private def initFourData {
        EventDao.saveEvent(eventDevoxx)
        EventDao.saveEvent(eventJava)
        EventDao.saveEvent(eventOther)
        EventDao.saveEvent(event4)
    }
}
