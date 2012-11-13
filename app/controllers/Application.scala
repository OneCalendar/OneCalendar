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

import play.api.mvc._
import models._
import dao.EventDao
import play.api.libs.json._
import java.util.Date
import service.{LoadDevoxx, LoadICalStream, ICalBuilder}
import collection.immutable.List


object Application extends OneCalendarController {

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def findPreviewByTags(keyWords: String) = Action {
        mongoConfigProd.now = new Date().getTime
        val tags: List[String] = keyWords.split(" ").toList
        val previewEvents: SearchPreview = EventDao.findPreviewByTag(tags)

        if (previewEvents.size > 0) Ok(Json.toJson(renderPreviewEventInJson(previewEvents))) else NotFound
    }

    @deprecated("devoxx must not be reloaded","march 2012")
    def loadDevoxxCalendar = Action {
        val url: String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad(url, "DEVOXX")
        Ok("base " + mongoConfigProd.dbName + " loaded with devoxx Calendar")
    }

    @deprecated("devoxx must not be reloaded","march 2012")
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

    private def renderPreviewEventInJson(previewEvents: SearchPreview): JsValue = {
        JsObject(
            List(
                ("size", JsNumber(previewEvents.size)),
                ("eventList", JsArray(previewEvents.events.map(previewEvent2Json)) )
            )
        )
    }

    private def previewEvent2Json(preview: Event): JsObject = {
        JsObject(List(
            ("event", JsObject(List(
                ("date", JsString(preview.begin.toString)),
                ("title", JsString(preview.title)),
                ("location", JsString(preview.location))
            )))))
    }
}