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

import java.lang.IllegalArgumentException
import play.api.libs.json._


object EventBriteParser extends EventbriteJsonReader {

    def parseEvents(json: String): Seq[EventbriteEvent] = {

        eventbriteResponseReader.reads(Json.parse(json)) match {
            case JsSuccess(events, _) =>
                events.asOpt.getOrElse(Nil)
            case JsError(errors) => throw new IllegalArgumentException("unknown response from eventbrite : " + errors)
            case _ => throw new IllegalStateException("unknown response from eventbrite : " + json)
        }

    }
}

case class EventbriteEvent(id: Option[String] = None,
                           title: Option[String] = None,
                           start_date: Option[String] = None,
                           description: Option[String] = None,
                           end_date: Option[String] = None,
                           tags: Option[String] = None,
                           timezone_offset: Option[String] = None,
                           url: Option[String] = None,
                           venue: Option[Venue] = None)

case class Venue(address: Option[String] = None,
                 address_2: Option[String] = None,
                 city: Option[String] = None,
                 region: Option[String] = None,
                 country: Option[String] = None,
                 postal_code: Option[String] = None)
