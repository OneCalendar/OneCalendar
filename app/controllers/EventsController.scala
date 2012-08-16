package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Event
import models.builder.EventBuilder
import dao.configuration.injection.MongoConfiguration
import dao.EventDao
import org.joda.time.DateTime

object EventsController extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    def addEvents = Action( Ok( views.html.addEvents() ) )

    def addSingleEvent = Action { implicit request =>
        val event:Event = eventForm.bindFromRequest.get

        EventDao.saveEvent(
            new EventBuilder()
                .uid( "1234567" )
                .location( event.location )
                .begin( new DateTime().plusDays(10) ) // TODO stub temporaire : une date + 10 jours
                .description( event.description )
                .end( new DateTime().plusDays(10) )    // TODO stub temporaire : une date + 10 jours
                .tags( event.tags )
                .title( event.title )
                .toEvent
        )

        Ok( "évènement " + event + " ajouté dans la base 'OneCalendar'" )
    }

    // TODO tous les champs sont obligatoires sauf description

    private val eventForm = Form(
        mapping(
            "title" -> text,
            /*"begin" -> date,
            "end" -> date,
            */
            "location" -> text,
            "description" -> text,
            "tags" -> text
        )( ( title, location, description/*, begin, end*/, tags ) => (
            new EventBuilder()
                .title( title )
                .location( location )
                .description( description )
                .tags( cleanTags( tags ) )
                .toEvent )
        )
         ( ( event: Event ) => Some( ( event.title, event.location, event.description, event.tags.mkString( "," ) ) ) )
    )

    private def cleanTags( tags: String ): List[ String ] = {
        tags.trim().toUpperCase.split(",").toList
    }
}