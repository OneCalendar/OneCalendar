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
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Application extends Controller with MongoDBProdContext with Event$VEventMapping with PreviewJsonWriter {

    def index = Action { Ok(views.html.index(Nil)) }

	def findByTags(keyWords: String)(implicit dao: EventDao = MongoDbEventDao, now: () => Long = () => DateTime.now.getMillis) = Action.async {
        dao.findByTags(splitTags(keyWords)).map(eventsAsIcs(_))
    }

    def findPreviewByTags(keyWords: String)(implicit dao: SearchPreviewDao = MongoDbSearchPreviewDao, now: () => Long = () => DateTime.now.getMillis) = Action.async {
	    println(splitTags(keyWords))
	    dao.findPreviewByTag(splitTags(keyWords)).map {
		    Option(_)
			    .filter( preview => preview.size > 0 )
			    .map( preview => Ok(Json.toJson(preview)).as("application/json") )
			    .getOrElse(NotFound)
	    }
    }

    def about = Action { Ok(views.html.about()) }

    def fetchCloudOfTags(implicit now: () => Long = () => DateTime.now.getMillis) = Action.async {
	    MongoDbEventDao.listTags().map { tags => Ok(Json.toJson(tags)).as("application/json") }
    }
    
    def eventCount = Action.async {
	    MongoDbEventDao.countFutureEvents().map { count => Ok(s"""{"eventNumber":"$count"}""").as("application/json") }
    }

	private def splitTags(keyWords: String) = URLDecoder.decode(keyWords,"UTF-8").split(" ").toSet

	private def eventsAsIcs(events: Set[Event]) = {
        events.toList match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(ICalendar.buildCalendar(events.toList)).as("text/calendar; charset=utf-8")
        }
    }
}
