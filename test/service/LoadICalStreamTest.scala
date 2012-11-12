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

package service

import org.scalatest.matchers.ShouldMatchers
import dao.EventDao
import com.mongodb.{Mongo, DB}
import org.scalatest.{BeforeAndAfter, FunSuite}
import dao.configuration.injection.MongoConfiguration
import models.Event
import org.joda.time.DateTime

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    implicit val mongoConfigurationTesting = MongoConfiguration( "test" )

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB( "test" )
        db
    }

    before {
        mongoConfigurationTesting.now = new DateTime().withDate(2012,4,1).getMillis
        db.requestStart
        db.getCollection("events").drop()
    }

    after {
        db.getCollection("test").drop()
        mongoConfigurationTesting.now = new DateTime().getMillis
        db.requestDone
    }

    test("should parse iCal stream") {
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
    
    test("should remove tags from event description"){
        val iCalService : LoadICalStream = new LoadICalStream()

        iCalService.getDescriptionWithoutTags("bla bla bla bla bla #toto") should be ("bla bla bla bla bla")
        iCalService.getDescriptionWithoutTags("bla bla bla bla bla #toto #titi #tata") should be ("bla bla bla bla bla")
        iCalService.getDescriptionWithoutTags("bla bla bla bla bla") should be ("bla bla bla bla bla")
    }
    
    test("should get tags from description"){
        val iCalService : LoadICalStream = new LoadICalStream()
        
        val tags : List[String] = iCalService.getTagsFromDescription("bla bla bla #Toto #tiTI")

        tags.size should  be (2)
        tags(0) should be ("TOTO")
        tags(1) should be ("TITI")
    }
}
