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

object Eventbrite  {

    type JsonResponse = String

    /** @param countryCode @see http://www.iso.org/iso/country_codes/iso_3166_code_lists/country_names_and_code_elements **/
    def request(keyWork: String, countryCode: Option[String] = None, licenseKey: String = "2Z5MEY5C4CD7D3FXUA"):  Seq[EventbriteEvent] = {
        val countryParam: String = countryCode match {
            case Some(code) => "&country=" + code
            case _ => ""
        }
        val url = "https://www.eventbrite.com/json/event_search" +
            "?app_key=" + licenseKey +
            "&keywords=" + keyWork +
            countryParam
        val response = new BufferedReader(new InputStreamReader( new URL(url).openStream() )).readLine()

        EventBriteParser.parseEvents(response)
    }

}


