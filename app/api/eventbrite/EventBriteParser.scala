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
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import com.codahale.jerkson.Json


object EventBriteParser extends Json {

    def parseEvents(json: String): Seq[EventbriteEvent] = {
        parse[EventbriteResponse](json) match {
            case EventbriteResponse(None, Some(eventbriteError)) =>
                throw new IllegalArgumentException(eventbriteError.error_type.get + " : " + eventbriteError.error_message.get)
            case EventbriteResponse(Some(events), _) => {
                events
                    .filter(_.event.isDefined)
                    .map(_.event.get)
            }
            case unknownResponse => throw new IllegalStateException("unknown response from eventbrite : " + json)
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class EventbriteResponse(events: Option[Seq[EventOrSummary]], error: Option[EventBriteError])

@JsonIgnoreProperties(ignoreUnknown = true)
case class EventOrSummary(event: Option[EventbriteEvent], summary: Option[Any])

@JsonIgnoreProperties(ignoreUnknown = true)
case class EventbriteEvent(id: Option[String],
                 title: Option[String],
                 start_date: Option[String],
                 description: Option[String],
                 end_date: Option[String],
                 tags: Option[String],
                 timezone_offset: Option[String],
                 url: Option[String],
                 venue: Option[Venue])

@JsonIgnoreProperties(ignoreUnknown = true)
case class Venue(address: Option[String],
                 address_2: Option[String],
                 city: Option[String],
                 region: Option[String],
                 country: Option[String],
                 postal_code: Option[String])

@JsonIgnoreProperties(ignoreUnknown = true)
case class EventBriteError(error_type: Option[String], error_message: Option[String])