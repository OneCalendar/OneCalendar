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

import java.io.{InputStreamReader, BufferedReader}
import java.net.URL
import models.Event
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.util.matching.Regex

object Eventbrite  {

    type JsonResponse = String

    /** @param countryCode @see http://www.iso.org/iso/country_codes/iso_3166_code_lists/country_names_and_code_elements **/
    def request(keyWord: String, countryCode: Option[String] = None, defaultTags: List[String], originalStream: String, licenseKey: String = "2Z5MEY5C4CD7D3FXUA"):  Seq[Event] = {
        val countryParam: String = countryCode match {
            case Some(code) => "&country=" + code
            case _ => ""
        }
        val url = "https://www.eventbrite.com/json/event_search" +
            "?app_key=" + licenseKey +
            "&keywords=" + keyWord +
            countryParam
        val response = new BufferedReader(new InputStreamReader( new URL(url).openStream() )).readLine()

        EventBriteParser.parseEvents(response).map(toEvent(_, defaultTags, originalStream))
    }

    val toEvent: (EventbriteEvent, List[String], String) => Event = {
        (eb, defaultTags, originalStream) => new Event(
            uid = eb.id.getOrElse(""),
            title = eb.title.getOrElse(""),
            begin = toDate(eb.start_date,eb.timezone_offset),
            end = toDate(eb.end_date,eb.timezone_offset),
            location = venueToLocation(eb.venue),
            description = cleanHtml(eb.description.getOrElse("")),
            tags = (defaultTags ::: toTags(eb.tags)).map(_.toUpperCase),
            url = eb.url,
            originalStream = Option(originalStream)
        )
    }

    def cleanHtml(content:String):String = {
        new Regex("""<.*?>""").replaceAllIn(content,"")
    }


    val venueToLocation: (Option[Venue]) => String = { opVenue =>
        opVenue match {
            case None => ""
            case Some(venue) => {
                List(venue.address, venue.address_2, venue.city, venue.postal_code, venue.region, venue.country)
                    .map(_.getOrElse(""))
                    .foldLeft("")((acc, s) => acc + " " + s)
            }
        }
    }

    val toTags: (Option[String]) => List[String] = { opTags =>
        opTags match {
            case None => Nil
            case Some(tags) =>
                tags.split("[,| ]")
                    .map(_.trim())
                    .filter(!_.trim().isEmpty )
                    .toList
        }
    }

    val toDate: (Option[String],Option[String]) => DateTime = { (opDate,opTimeZone) =>
        (opDate,opTimeZone) match {
            case (None,_) => null
            case (Some(date),Some(timeZone)) => DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss'GMT'Z").withOffsetParsed().parseDateTime(date + timeZone)
        }
    }

}


