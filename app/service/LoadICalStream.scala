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

import api.icalendar._
import dao.EventDao._
import dao.configuration.injection.MongoPoolProperties.MongoDbName
import java.net.URL
import models.Event
import models.Event._
import play.api.Logger
import scala.Left
import scala.Right
import com.mongodb.casbah.MongoDB

class LoadICalStream {

    def parseLoad(url: String, streamTags: List[String] = Nil)(implicit now: () => Long, dbName: MongoDbName,  pool: MongoDB) {

        ICalendar.retrieveVEvents(new URL(url).openStream) match {
            case Right(vevents) =>
                deleteByOriginalStream(url)

                val (toSave, passed): (List[Event], List[Event]) = vevents
                    .map(vevent => buildEvent(url, vevent, streamTags))
                    .partition(event => event.end.isAfter(now()))

                saveEvents(toSave, url)
                reportNotLoadedEvents(passed, url)
                
            case Left(ICalendarParsingError(message, exception)) => Logger.warn(message + " from " + url + " : " + exception.getMessage)
        }
    }

    private def buildEvent(url: String, vEvent: VEvent, streamTags: List[String]): Event = {
        Event(
            uid = vEvent.uid.getOrElse(""),
            title = vEvent.summary.getOrElse(""),
            begin = vEvent.startDate.get,
            end = vEvent.endDate.get,
            location = vEvent.location.getOrElse(""),
            url = vEvent.url,
            originalStream = Option(url),
            description = vEvent.description.getOrElse(""),
            tags = getTagsFromDescription(vEvent.description.getOrElse("") + extractTagsFromStreamTags(streamTags))
        )
    }

    private def saveEvents(toSave: List[Event], url: String)(implicit now: () => Long, dbName: MongoDbName,  pool: MongoDB) {
        toSave foreach ( saveEvent )
        Logger.info("%d events loaded from %s".format(toSave.length, url))
    }

    private def reportNotLoadedEvents(notLoadedEvent: List[Event], url:String)(implicit now: () => Long) =
        if( !notLoadedEvent.isEmpty ) Logger.info("%d already ended events not loaded from %s".format(notLoadedEvent.length, url))

    private def extractTagsFromStreamTags(streamTags: List[String]): String =
        if( streamTags.isEmpty ) "" else " #" + streamTags.mkString("#")
}