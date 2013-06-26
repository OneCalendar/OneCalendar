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
import models.Event
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import dao.EventDao
import com.github.simplyscala.MongoEmbedDatabase
import com.mongodb.casbah.{MongoConnection, MongoDB}
import com.mongodb.{ServerAddress, MongoOptions}
import dao.framework.MongoConnectionProperties
import MongoConnectionProperties._
import com.github.simplyscala.MongodProps

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll with MongoEmbedDatabase {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27020) }
    override def afterAll() { mongoStop(mongoProps) }

    implicit val dbName: MongoDbName = "test"
    implicit val pool: MongoDB = {
        val connection: MongoConnection = {
            val options: MongoOptions = new MongoOptions()
            options.setConnectionsPerHost(2)
            MongoConnection(new ServerAddress("127.0.0.1", 27020), options)
        }

        connection(dbName)
    }

    val url : String = "https://www.google.com/calendar/ical/cs98tardtttjejg93tpcb71ol6nvachq%40import.calendar.google.com/public/basic.ics"

    test("should parse iCal stream") {
        implicit val now = () => new DateTime().withDate(2012,4,1).getMillis

        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, List("DEVOXX") )

        val events: List[Event] = EventDao.findAll()
        val count: Int = events.size

        count should be > 50
        count should be > 50
        count should be < 100
        events.head.tags should contain ("DEVOXX")
    }

    test("should parse iCal stream with two stream tags") {
        implicit val now = () => new DateTime().withDate(2012,4,1).getMillis

        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, List("DEVOXX","TEST") )

        val events: List[Event] = EventDao.findAll()
        val count: Int = events.size

        count should be > 50
        count should be > 50
        count should be < 100
        events.head.tags should be (List("DEVOXX", "TEST"))
    }
}