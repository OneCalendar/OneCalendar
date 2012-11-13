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

import org.scalatest.matchers._
import org.joda.time.DateTime
import org.scalatest._
import models.Event

class ICalBuilderTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    var ical: String = _

    before {
        val events: List[Event] = List(
            Event(
                uid = "0",
                title = "Event1",
                begin = new DateTime(),
                end = new DateTime(),
                location = "place1",
                description = "super java conf",
                tags = List("java")
            ),

            Event(
                uid = "1",
                title = "Event2",
                begin = new DateTime(),
                end = new DateTime(),
                location = "place2",
                description = "super scala conf",
                tags = List("scala")
            )
        )

        ical = new ICalBuilder().buildCalendar(events)
    }

    test("ical should be a valid iCal") {
        ical should startWith ("BEGIN:VCALENDAR")
        ical should include ("VERSION:2.0")
        ical should include ("PRODID:")
    }

    test("ical should have all events") {
        ical should include ("SUMMARY:Event1")
        ical should include ("SUMMARY:Event2")
    }
    
    test("event should have all properties") {
        val event = new ICalBuilder().buildCalendar(
            List( Event(
                uid = "0",
                title = "Event1",
                begin = new DateTime( 2010, 01, 01, 12, 0, 0 ),
                end = new DateTime( 2010, 01, 01, 14, 0, 0 ),
                location = "place1",
                description = "super java conf",
                tags = List("java")
            ))
        )

        event should include ( "DTSTART:20100101T120000" )
        event should include ( "DTEND:20100101T140000" )
        event should include ( "DESCRIPTION:super java conf" )
        event should include ( "LOCATION:place1" )
        event should include ( "UID:0" )
    }
}