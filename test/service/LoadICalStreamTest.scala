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

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    implicit val mongoConfigurationTesting = MongoConfiguration( "test" )

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB( "test" )
        db
    }

    before {
        db.requestStart
    }

    after {
        db.requestDone
    }

    ignore("should parse iCal stream") {
        val url : String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, "DEVOXX" )

        val events: List[Event] = EventDao.findAll()
        val count: Int = events.size

        count should be > 50
        count should be < 100
        events.head.tags should contain ("DEVOXX")
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
