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

package controllers

import play.api.mvc._
import models._
import dao.configuration.injection.MongoConfiguration
import dao.EventDao
import play.api.libs.json._
import java.util.Date
import service.{LoadDevoxx, LoadICalStream, ICalBuilder}

object Application extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def findPreviewByTags(keyWords: String) = Action {
        mongoConfigProd.now = new Date().getTime();
        val tags: List[String] = keyWords.split(" ").toList
        val previewEvents: SearchPreview = EventDao.findPreviewByTag(tags)
        Ok(Json.toJson(renderPreviewEventInJson(previewEvents)))
    }

    def loadDevoxxCalendar = Action {
        val url: String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad(url, "DEVOXX")
        Ok("base " + mongoConfigProd.dbName + " loaded with devoxx Calendar")
    }

    def loadDevoxxCfp = Action {
        var devoxx: LoadDevoxx = new LoadDevoxx()
        devoxx.parseLoad()
        Ok("base " + mongoConfigProd.dbName + " loaded with devoxx Calendar")
    }

    def about = Action {
        Ok(views.html.about())
    }
    
    def fetchCloudOfTags = Action {
        Ok(Json.toJson(EventDao.listTags())).as("application/json")
    }
    
    private def renderEvents( events: List[ Event ] ) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(calendarService.buildCalendar(events)).as("text/calendar; charset=utf-8")
        }
    }

    private def previewEvent2Json(preview: Event): JsObject = {
        JsObject(List(
            ("event", JsObject(List(
                ("date", JsString(preview.begin.toString)),
                ("title", JsString(preview.title)),
                ("location", JsString(preview.location))
            )))))
    }

    private def renderPreviewEventInJson(previewEvents: SearchPreview): JsValue = {
        JsObject(
            List(
                ("size", JsNumber(previewEvents.size)),
                ("eventList", JsArray(
                    List(
                        previewEvent2Json(previewEvents.events(0))
                        , previewEvent2Json(previewEvents.events(1))
                        , previewEvent2Json(previewEvents.events(2))
                    )
                ))
            )
        )
    }
}