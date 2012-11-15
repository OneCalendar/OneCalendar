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

import com.codahale.jerkson.Json
import dao._
import models._
import java.net._
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import collection.Seq
import collection.immutable.List
import play.api.Logger
import models.DevoxxSchedule
import models.DevoxxEvents
import models.DevoxxPresentation
import dao.configuration.injection.MongoPool.MongoDbName

object LoadDevoxx extends Json with NowEventInjection {

    val log = Logger("EventDao")
    val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S")
    val DB_NAME: String = "OneCalendar"

    def parseLoad()(implicit dbName: MongoDbName) {
        val devoxxEvents = "https://cfp.devoxx.com/rest/v1/events/"

        val events: Seq[DevoxxEvents] = parseUrl[Seq[DevoxxEvents]](devoxxEvents)

        events
            .map(event => "https://cfp.devoxx.com/rest/v1/events/%s/schedule".format(event.id))
            .foreach(load)
    }

    def load(devoxxUrl: String)(implicit dbName: MongoDbName) {
        EventDao.deleteByOriginalStream(devoxxUrl)

        val schedules: Seq[DevoxxSchedule] = parseUrl[Seq[DevoxxSchedule]](devoxxUrl)

        val shedulesSet = Set(schedules.toArray: _*)

        shedulesSet.foreach(schedule => {
            if ( schedule.presentationUri.isDefined ) {
                try {
                    val presentation: DevoxxPresentation = parseUrl[DevoxxPresentation](schedule.presentationUri.get.replace("http://", "https://"))

                    var curTags: List[String] = List("DEVOXX")
                    presentation.tags.foreach(tag => {
                        curTags = curTags :+ ( tag.name.toUpperCase )
                    })
                    val event: Event = Event(
                        uid = schedule.presentationUri.get,
                        title = presentation.title,
                        begin = pattern.parseDateTime(schedule.fromTime.get),
                        end = pattern.parseDateTime(schedule.toTime.get),
                        description = presentation.summary,
                        location = presentation.room.get,
                        originalStream = devoxxUrl,
                        tags = curTags
                    )
                    EventDao.saveEvent(event)
                } catch {
                    case e: Exception => log.warn("the presentation %s can't be load".format(schedule.presentationUri))
                }
            }

        })
    }

    def parseUrl[A](url: String)(implicit mf: Manifest[A]): A = parse[A](new URL(url).openStream())
}