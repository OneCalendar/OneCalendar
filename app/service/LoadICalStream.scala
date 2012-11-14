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
import play.api.Logger
import models.Event
import api.icalendar.{VEvent, ICalendar}
import dao.EventDaoBis
import org.joda.time.DateTime
import models.Event._
import com.mongodb.casbah.Imports._
import scala.Left
import api.icalendar.ICalendarParsingError
import dao.configuration.injection.MongoConfiguration
import scala.Right

class LoadICalStream {

    val DB_NAME : String = "OneCalendar"

    def parseLoad(url: String, defaultStreamTag: String = "" )( implicit now: () => Long,collection : String => MongoCollection ) {

        EventDaoBis.deleteByOriginalStream(url)

        ICalendar.retrieveVEvents(new URL(url).openStream) match {
            case Right(vevents) =>
                val (toSave, passed): (List[Event], List[Event]) = vevents
                        .map( vevent => buildEvent(url, vevent, defaultStreamTag) )
                        .span( event => event.end.isAfter(now()) )

                saveEvents(toSave)

                reportNotLoadedEvents(passed)
                
            case Left(ICalendarParsingError(message, exception)) => Logger.warn(message + " : " + exception.getMessage)
        }
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

    private def saveEvents(toSave: scala.List[ Event ])(implicit now: () => Long, collection : String => MongoCollection) {
        toSave foreach ( EventDaoBis.saveEvent )
        Logger.info("%d events loaded".format(toSave.length))
    }

    private def reportNotLoadedEvents(notLoadedEvent: List[Event])(implicit now: () => Long) {
        if ( !notLoadedEvent.isEmpty ) Logger.warn("%d events not loaded ".format(notLoadedEvent.length))
        notLoadedEvent.foreach(event => Logger.warn("event %s not loaded because now is %s and it's already ended %s".format(event.title, new DateTime(now()), event.end)))
    }
}