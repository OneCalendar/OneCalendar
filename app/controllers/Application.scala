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

package controllers

import api.icalendar.ICalendar
import dao._
import models.mapping.Event$VEventMapping
import models._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Writes._

case class PreviewEvent(date: String, title: String, location: String)
case class Preview (size: Long, eventList: Seq[PreviewEvent])

object Application extends OneCalendarController with Event$VEventMapping with PreviewJsonWriter {

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def findPreviewByTags(keyWords: String)(implicit dao: EventDaoTrait = EventDao, now: () => Long = () => DateTime.now.getMillis) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        val searchPreview: SearchPreview = dao.findPreviewByTag(tags)

        val previewEvents = searchPreview.previewEvents.map(
            e => PreviewEvent(date = e.begin.toString(),title = e.title,location = e.location)
        )

        Option(searchPreview)
            .map( p => Preview(size = p.totalEventNumber, eventList= previewEvents))
            .filter( preview => preview.size > 0 )
            .map( preview => Ok(Json.toJson(preview)).as("application/json") )
            .getOrElse(NotFound)
    }

    def about = Action { Ok(views.html.about()) }

    def fetchCloudOfTags(implicit now: () => Long = () => DateTime.now.getMillis) = Action {
        Ok(Json.toJson(EventDao.listTags())).as("application/json")
    }
    
    def eventCount(implicit now: () => Long = () => DateTime.now.getMillis) = Action {
        Ok("""{"eventNumber":"%s"}""".format(EventDao.countFutureEvents)).as("application/json")
    }

    private def renderEvents(events: List[Event]) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(ICalendar.buildCalendar(events)).as("text/calendar; charset=utf-8")
        }
    }
}