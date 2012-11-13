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
import dao.EventDao
import dao.configuration.injection.MongoConfiguration
import java.util.StringTokenizer
import play.api.Logger
import models.Event
import api.icalendar.{VEvent, ICalendarParsingError, ICalendar}
import dao.EventDao.saveEvent
import org.joda.time.DateTime

class LoadICalStream {
    
    val TAG_PATTERN : String = "#([\\w\\d\\p{L}]+)"
    val DB_NAME : String = "OneCalendar"

    def parseLoad(url: String, defaultStreamTag: String = "" )( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteByOriginalStream(url)

        ICalendar.retrieveVEvents(new URL(url).openStream) match {
            case Right(vevents) =>
                val (toSave, passed): (List[Event], List[Event]) = vevents
                        .map( vevent => buildEvent(url, vevent, defaultStreamTag) )
                        .span( event => event.end.isAfter(dbConfig.now) )

                saveEvents(toSave)

                reportNotLoadedEvents(passed)
                
            case Left(ICalendarParsingError(message, exception)) => Logger.warn(message + " : " + exception.getMessage)
        }
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

    private def buildEvent(url: String, vEvent: VEvent, defaultStreamTag: String): Event = {
        Event(
            uid = vEvent.uid.getOrElse(""),
            title = vEvent.summary.getOrElse(""),
            begin = vEvent.startDate.get,
            end = vEvent.endDate.get,
            location = vEvent.location.getOrElse(""),
            url = vEvent.url.getOrElse(""),
            originalStream = url,
            description = vEvent.description.getOrElse(""),
            tags = getTagsFromDescription(vEvent.description.getOrElse("") + ( if ( !defaultStreamTag.isEmpty ) " #" + defaultStreamTag; else "" ))
        )
    }

    private def saveEvents(toSave: scala.List[ Event ])(implicit dbConfig: MongoConfiguration) {
        toSave foreach ( saveEvent )
        Logger.info("%d events loaded".format(toSave.length))
    }

    private def reportNotLoadedEvents(notLoadedEvent: List[Event])(implicit dbConfig: MongoConfiguration) {
        if ( !notLoadedEvent.isEmpty ) Logger.warn("%d events not loaded ".format(notLoadedEvent.length))
        notLoadedEvent.foreach(event => Logger.warn("event %s not loaded because now is %s and it's already ended %s".format(event.title, new DateTime(dbConfig.now), event.end)))
    }
}
