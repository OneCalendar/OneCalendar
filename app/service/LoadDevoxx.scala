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
import dao.configuration.injection.MongoConfiguration._
import com.codahale.jerkson.Json
import dao._
import models._
import java.net._

class LoadDevoxx extends Json {


    val DB_NAME : String = "OneCalendar"

    def parseLoad() ( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteAll()

        val schedules: Seq[DevoxxSchedule] = parse[Seq[DevoxxSchedule]](new URL("https://cfp.devoxx.com/rest/v1/events/6/schedule").openStream)

        schedules.foreach(schedule => {
            val presentation: DevoxxPresentation = parseUrl[DevoxxPresentation](schedule.presentationUri.get)


//              var event:Event = new Event(schedule.id,presentation.title,presentation,schedul,Nil,);
//            presentation.
        })
    }

    def parseUrl[A](url:String)(implicit mf: Manifest[A]):A = {
        parse[A](new URL(url).openStream())
    }

}