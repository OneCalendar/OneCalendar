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

import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.{Json, JsValue}
import play.api.libs.concurrent.Akka
import akka.actor.{Props, Actor}
import play.api.libs.EventSource.EventNameExtractor
import play.api.libs.EventSource
import play.api.Play.current

object EventProducer {
    def apply[T](playProducer: (Enumerator[T], Channel[T])) = new EventProducer(playProducer)
}

class EventProducer[T](playProducer: (Enumerator[T], Channel[T])) {
    def enumerator: Enumerator[T] = playProducer._1
    def channel: Channel[T] = playProducer._2
}

class TestActor(eventProcuder: EventProducer[JsValue]) extends Actor {
    def receive = {
        case ICalEventAdded(event) => eventProcuder.channel.push(Json.toJson(Map("eventNumber" -> event)))
    }
}

case class ICalEventAdded(eventNumber: Int)

object EventsController extends Controller with MongoDBProdContext {

    val buildEventProducer = EventProducer(Concurrent.broadcast[JsValue])

    val testActor = Akka.system.actorOf(Props(new TestActor(buildEventProducer)), "testActor")

    implicit val now = () => DateTime.now.getMillis

    def addEvents = Action( Ok( views.html.addEvents() ) )

    def addSingleEvent = Action { implicit request =>
        val event = eventForm.bindFromRequest.get
        EventDao.saveEvent(event)

        Ok( "évènement " + event + " ajouté dans la base 'OneCalendar'" )
    }

    def allEvents = Action {
        val events = EventDao.findAllFromNow()
            .map( event => event.copy(tags = event.tags.distinct) )  // TODO régler le problème à la source <=> mettre un Set sur tags et supprimé les doublons à l'écriture
            .sortWith { (e1,e2) => e1.begin.compareTo(e2.begin) < 0 }

        Ok( views.html.index(events) )
    }

    def eventCountSSE(implicit now: () => Long = () => DateTime.now.getMillis) = Action {
        implicit val eventNameExtractor = EventNameExtractor[JsValue]( (event) => event.\("event").asOpt[String] )

        //val eventNumber = Json.toJson(Map("eventNumber" -> EventDao.countFutureEvents))

        //Ok.feed(Enumerator.apply(eventNumber) &> EventSource()).as("text/event-stream")
        Ok.feed(buildEventProducer.enumerator &> EventSource()).as("text/event-stream")
    }

    def addEvent(eventNumber: Int) = Action {
        testActor ! ICalEventAdded(eventNumber)
        Ok("cool raoul !")
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