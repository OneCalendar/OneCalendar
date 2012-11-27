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

package api.eventbrite

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import models.Event
import org.joda.time.{DateTimeZone, DateTime}
import api.eventbrite.Eventbrite.toEvent

class Eventbrite2EventMapperTest extends FunSuite with ShouldMatchers {

    test("should map id") {
        val eb = EventbriteEvent(id = Some("id"))
        val event: Event = toEvent(eb, Nil, "")
        event.uid should be ("id")
    }

    test("should map title") {
        val eb = EventbriteEvent(title = Some("title"))
        val event: Event = toEvent(eb, Nil, "")
        event.title should be ("title")
    }

    test("should map description without html") {
        val eb = EventbriteEvent(
            description = Some("""<P><SPAN STYLE=\\\"font-family: tahoma, arial, helvetica, sans-serif; color: #003366; font-size: x-large;\\\">Summary</SPAN></P>""")
        )
        val event: Event = toEvent(eb, Nil, "")
        event.description should be ("Summary")
    }

    test("should map description") {
        val eb = EventbriteEvent(
            description = Some("""<P><SPAN STYLE=\\\"font-family: tahoma, arial, helvetica, sans-serif; color: #003366; font-size: x-large;\\\">Summary</SPAN></P>""")
        )
        val event: Event = toEvent(eb, Nil, "")
        event.description should be ("Summary")
    }

    test("should map begin") {
        val eb = EventbriteEvent(
            start_date = Some("2013-01-28 09:04:05"),
            end_date = Some("2020-01-01 00:00:00"),
            timezone_offset = Some("GMT-0500")
        )
        val event: Event = toEvent(eb, Nil, "")
        println(event.begin)
        event.begin should be (new DateTime(2013, 01, 28, 9, 4, 5, 0, DateTimeZone.forOffsetHours(-5) ))
    }

    test("should map end") {
        val eb = EventbriteEvent(
            start_date = Some("2020-01-01 00:00:00"),
            end_date = Some("2013-01-28 09:02:01"),
            timezone_offset = Some("GMT-0500")
        )
        val event: Event = toEvent(eb, Nil, "")
        event.end should be (new DateTime(2013, 01, 28, 9, 2, 1, 0, DateTimeZone.forOffsetHours(-5) ))
    }

    test("should map filled location") {
        val eb = EventbriteEvent(
            venue = Some(Venue(
                address = Some("addresse1"),
                address_2 = Some("addresse2"),
                city = Some("city"),
                region = Some("region"),
                country = Some("country"),
                postal_code = Some("postal_code")
            ))
        )
        val event: Event = toEvent(eb, Nil, "")
        event.location should include ("addresse1")
        event.location should include ("addresse2")
        event.location should include ("city")
        event.location should include ("region")
        event.location should include ("country")
        event.location should include ("postal_code")
    }

    test("should map tags") {
        val eb = EventbriteEvent(tags = Some("programming, paris scala user group"))
        val event: Event = toEvent(eb, List("default"), "")
        event.tags should be (List("DEFAULT","PROGRAMMING", "PARIS", "SCALA", "USER", "GROUP"))
    }

    test("should map url") {
        val eb = EventbriteEvent(url = Some("http://psug-27-SRCH.eventbrite.com"))
        val event: Event = toEvent(eb, Nil, "")
        event.url should be (Some("http://psug-27-SRCH.eventbrite.com"))
    }

    test("should have originalStream = keyword") {
        val eb = EventbriteEvent()
        val event: Event = toEvent(eb, Nil, "keyword")
        event.originalStream should be (Some("keyword"))
    }


}
