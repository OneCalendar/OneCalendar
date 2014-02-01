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
import java.net.URLDecoder
import play.api.libs.EventSource.EventNameExtractor
import play.api.libs.EventSource
import play.api.libs.iteratee.Enumerator

case class PreviewEvent(date: String, title: String, location: String)
case class Preview (size: Long, eventList: Seq[PreviewEvent])

object Application extends Controller with MongoDBProdContext with Event$VEventMapping with PreviewJsonWriter {

	implicit val implicitJSonWrites = new Writes[List[Event]] {
		def writes(events: List[Event]): JsValue = {
			Json.arr(events.map (e =>
				Json.obj(
					"uid" -> e.uid,
					"title" -> e.title,
					"begin" -> e.begin,
					"end" -> e.end,
					"location" -> e.location,
					"description" -> e.description,
					"tags" -> e.tags,
					"originalStream" -> e.originalStream,
					"url" -> e.url
				)
			))
		}
	}

    def index = Action {
        Ok(views.html.index(Nil))
    }

    def splitTags(keyWords: String) = {
        URLDecoder.decode(keyWords,"UTF-8").split(" ").toList
    }

    def findByTags(keyWords: String)(implicit now: () => Long = () => DateTime.now.getMillis) = Action {
        renderEvents(EventDao.findByTag(splitTags(keyWords)))
    }
    def findWithoutTags() = Action {
        renderEvents(EventDao.findAll())
    }

    def findPreviewByTags(keyWords: String)(implicit dao: EventDaoTrait = EventDao, now: () => Long = () => DateTime.now.getMillis) = Action {
        val searchPreview: SearchPreview = dao.findPreviewByTag(splitTags(keyWords))

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

	def findByIdsAndTags(ids: String, tags: String)(implicit dao: EventDaoTrait = EventDao, now: () => Long = () => DateTime.now.getMillis) = Action {
		request =>
			request.headers.get("Accept") match {
				case _ => Ok(Json.toJson(dao.findByIdsAndTags(splitTags(ids), splitTags(tags))))
			}
	}

	private def renderEvents(events: List[Event]) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(ICalendar.buildCalendar(events)).as("text/calendar; charset=utf-8")
        }
    }
}
