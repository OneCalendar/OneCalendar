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
import EventBriteParser.parseEvents

class EventbriteParserTest extends FunSuite with ShouldMatchers {

    test("should map 1 dummy event") {
        val events: Seq[EventbriteEvent] = parseEvents("""{"events":[ {"event": { "id":4737033595 } } ] }""")
        events.size should be (1)
        events.head.id should be(Option("4737033595"))
    }

    ignore("should understand no event found") {
        val response = parseEvents("""{"events": [], "error": {"error_type": "Not Found", "error_message": "No events found matching the following criteria. [organizer=pppppppppppppp,  ]"}}""")
        response should be (Nil)
    }

    ignore("unknown response from eventbrite should throw IllegalStateException") {
        val thrown = evaluating {
            parseEvents("""{"toto": "titi"}""")
        } should produce [IllegalStateException]
        thrown.getMessage should include ("""{"toto": "titi"}""")
    }
}