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

import dao.configuration.injection._
import com.codahale.jerkson.Json
import dao._
import models._
import builder.EventBuilder
import java.net._
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import collection.Seq
import collection.immutable.List

class LoadDevoxx extends Json {

    val DB_NAME: String = "OneCalendar"

    def parseLoad()(implicit dbConfig: MongoConfiguration = MongoConfiguration(DB_NAME)) {

        EventDao.deleteAll()

        val schedules: Seq[DevoxxSchedule] = parseUrl[Seq[DevoxxSchedule]]("https://cfp.devoxx.com/rest/v1/events/6/schedule")
        val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S")

        schedules.foreach(schedule => {
            if (schedule.presentationUri.isDefined) {
                val presentation: DevoxxPresentation = parseUrl[DevoxxPresentation](schedule.presentationUri.get.replace("http://", "https://"))

                var curTags: List[String] = List("DEVOXX")
                presentation.tags.foreach(tag => {
                    curTags = curTags :+ (tag.name.toUpperCase)
                })
                val event: Event = new EventBuilder()
                    .uid(schedule.presentationUri.get)
                    .title( presentation.title)
                    .begin(pattern.parseDateTime(schedule.fromTime.get))
                    .end(pattern.parseDateTime(schedule.toTime.get))
                    .description(presentation.summary)
                    .location(presentation.room.get)
                    .tags(curTags)
                    .toEvent

                EventDao.saveEvent(event)
            }
        })
    }

    def parseUrl[A](url: String)(implicit mf: Manifest[A]): A = {
        parse[A](new URL(url).openStream())
    }

}