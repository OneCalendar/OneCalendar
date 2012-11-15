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

import java.net.URL
import dao.EventDao
import dao.configuration.injection.MongoConfiguration
import play.api.Logger
import models.Event
import api.icalendar.{VEvent, ICalendarParsingError, ICalendar}
import dao.EventDao.saveEvent
import org.joda.time.DateTime
import models.Event._

class LoadICalStream {

    val DB_NAME : String = "OneCalendar"

    def parseLoad(url: String, streamTags: List[String] = Nil )( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteByOriginalStream(url)

        ICalendar.retrieveVEvents(new URL(url).openStream) match {
            case Right(vevents) =>
                val (toSave, passed): (List[Event], List[Event]) = vevents
                        .map( vevent => buildEvent(url, vevent, streamTags) )
                        .span( event => event.end.isAfter(dbConfig.now()) )

                saveEvents(toSave, url)

                reportNotLoadedEvents(passed, url)
                
            case Left(ICalendarParsingError(message, exception)) => Logger.warn(message + " : " + exception.getMessage)
        }
    }

    private def buildEvent(url: String, vEvent: VEvent, streamTags: List[String]): Event = {
        Event(
            uid = vEvent.uid.getOrElse(""),
            title = vEvent.summary.getOrElse(""),
            begin = vEvent.startDate.get,
            end = vEvent.endDate.get,
            location = vEvent.location.getOrElse(""),
            url = vEvent.url.getOrElse(""),
            originalStream = url,
            description = vEvent.description.getOrElse(""),
            tags = getTagsFromDescription(vEvent.description.getOrElse("") + extractTagsFromStreamTags(streamTags))
        )
    }

    private def saveEvents(toSave: scala.List[ Event ], url:String)(implicit dbConfig: MongoConfiguration) {
        toSave foreach ( saveEvent )
        Logger.info("%d events loaded from %s".format(toSave.length, url))
    }

    private def reportNotLoadedEvents(notLoadedEvent: List[Event], url:String)(implicit dbConfig: MongoConfiguration) {
        if ( !notLoadedEvent.isEmpty ) Logger.warn("%d events not loaded from %s".format(notLoadedEvent.length, url))
        notLoadedEvent.foreach(event => Logger.warn("event %s not loaded because now is %s and it's already ended %s".format(event.title, new DateTime(dbConfig.now), event.end)))
    }

    private def extractTagsFromStreamTags(streamTags: List[String]): String =
        if ( streamTags.isEmpty ) "" else " #" + streamTags.mkString("#")
}