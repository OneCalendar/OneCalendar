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

import dao.EventDao
import models.Event
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

object EventsController extends OneCalendarController {

    def addEvents = Action( Ok( views.html.addEvents() ) )

    def addSingleEvent = Action { implicit request =>
        val event:Event = eventForm.bindFromRequest.get
        EventDao.saveEvent(event)
        Ok( "évènement " + event + " ajouté dans la base 'OneCalendar'" )
    }

    // TODO tous les champs sont obligatoires sauf description

    private val eventForm = Form(
        mapping(
            "title" -> text,
            "begindate" -> date,
            "beginhour" -> nonEmptyText (5, 5),
            "enddate" -> date,
            "endhour" -> nonEmptyText (5, 5),
            "location" -> text,
            "description" -> text,
            "tags" -> text
        )( ( title, begindate, beginhour, endate, endhour, location, description, tags ) => (
            Event(
                title = title,
                location = location,
                description = description,
                begin = new DateTime( begindate ).plusHours( beginhour.split( ":" )(0).toInt ).plusMinutes( beginhour.split( ":" )(1).toInt ),
                end = new DateTime( endate ).plusHours( endhour.split( ":" )(0).toInt ).plusMinutes( endhour.split( ":" )(1).toInt ),
                tags = cleanTags( tags )
            ))
        )
         ( ( event: Event ) => Some( ( event.title, event.begin.toDate, "12345", event.end.toDate, "12345", event.location, event.description, event.tags.mkString( "," ) ) ) )
    )

    private def cleanTags( tags: String ): List[ String ] = {
        tags.trim().toUpperCase.split(",").toList
    }
}