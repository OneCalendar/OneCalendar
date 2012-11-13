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

import java.net.URL
import org.joda.time.DateTime
import dao.EventDao
import dao.configuration.injection.MongoConfiguration
import java.util.StringTokenizer
import models.builder.EventBuilder
import play.api.Logger
import api.icalendar.ICalendar

class LoadICalStream {
    
    val TAG_PATTERN : String = "#([\\w\\d\\p{L}]+)"
    val DB_NAME : String = "OneCalendar"

    def parseLoad(url: String, eventName: String = "" )( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteByOriginalStream(url)
        val urlCal = new URL(url)

        var nb = 0

        ICalendar.retrieveVEvents(urlCal.openStream()) match {
            case Right(vevents) => vevents.foreach(vEvent => {
                val event = new EventBuilder()
                    .uid( vEvent.uid.getOrElse("") )
                    .title( vEvent.summary.getOrElse("") )
                    .begin( vEvent.startDate.get )
                    .end( vEvent.endDate.get )
                    .location( vEvent.location.getOrElse("") )
                    .url( vEvent.url.getOrElse("") )
                    .originalStream(url)
                    .description( vEvent.description.getOrElse("") )
                    .tags( getTagsFromDescription(vEvent.description.getOrElse("") + (if(!eventName.isEmpty) " #" + eventName; else ""  ) ) )
                    .toEvent

                if (event.end.isAfter(dbConfig.now)) {
                    nb=nb+1
                    EventDao.saveEvent( event )
                } else {
                    Logger.warn("event %s not loaded because now is %s and it's already ended %s".format(event.title,new DateTime(dbConfig.now),event.end) )
                }
            })
            case Left(error) => Logger.warn(error.message + " : " + error.e.getMessage)
        }
        Logger.trace("been loaded %d events".format (nb))
    }

    def getDescriptionWithoutTags(s: String):String = {
        val description : String = s.replaceAll(TAG_PATTERN,"")
        description.trim()
    }

    def getTagsFromDescription(s: String): scala.List[String] = {
        var tags : List[String]= List()
        val tokenizer: StringTokenizer = new StringTokenizer(s)
        while (tokenizer.hasMoreTokens()) {
            var token : String = tokenizer.nextToken()
            if(token.matches(TAG_PATTERN)){
                tags=tags:+(token.replace("#","").trim().toUpperCase())
            }
        }
        tags
    }
}
