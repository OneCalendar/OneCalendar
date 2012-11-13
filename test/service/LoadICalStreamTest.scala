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

package service

import org.scalatest.matchers.ShouldMatchers
import dao.EventDao
import com.mongodb.{Mongo, DB}
import org.scalatest.{BeforeAndAfter, FunSuite}
import dao.configuration.injection.MongoConfiguration
import models.Event
import org.joda.time.DateTime

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB( "test" )
        db
    }

    before {
        db.requestStart
        db.getCollection("events").drop()
    }

    after {
        db.getCollection("test").drop()
        db.requestDone
    }

    test("should parse iCal stream") {
        implicit val mongoConfigurationTesting = MongoConfiguration( "test", () => new DateTime().withDate(2012,4,1).getMillis )

        val url : String = "https://www.google.com/calendar/ical/cs98tardtttjejg93tpcb71ol6nvachq%40import.calendar.google.com/public/basic.ics"
        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, "DEVOXX" )

        val events: List[Event] = EventDao.findAll()
        val count: Int = events.size

        count.should(be.>(50))
        count should be > 50
        count should be < 100
        events.head.tags should contain ("DEVOXX")


        List(1,2,3).should(contain (2))
       	List(1,2,3).should(not.contain(4))
       	List(1,2,3).should(have.size(3))
    }
}