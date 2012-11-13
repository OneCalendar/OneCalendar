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
import dao._
import service.ICalBuilder
import collection.immutable.List
import com.codahale.jerkson.Json


object Application extends OneCalendarController with Json {

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def findPreviewByTags(keyWords: String)(implicit dao:EventDaoTrait = EventDao) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        val searchPreview: SearchPreview = dao.findPreviewByTag(tags)

        val es = searchPreview.previewEvents.map(
            e => Map("event" -> Map(
                            "date" -> e.begin.toString(),
                            "title" -> e.title,
                            "location" -> e.location
                            )
            )
        )

        val previewJson: String = generate(Option(searchPreview).map(
          p => Map(
            "size" -> p.totalEventNumber,
            "eventList" -> es
          ))
        )
        if (searchPreview.totalEventNumber > 0) {
          Ok(previewJson).as("application/json")
        }
        else NotFound
    }

    def about = Action {
        Ok(views.html.about())
    }
    
    def fetchCloudOfTags = Action {
        Ok(generate(EventDao.listTags())).as("application/json")
    }
    
    private def renderEvents( events: List[ Event ] ) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(calendarService.buildCalendar(events)).as("text/calendar; charset=utf-8")
        }
    }
}