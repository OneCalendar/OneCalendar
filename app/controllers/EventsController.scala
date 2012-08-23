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
                .begin( event.begin )
                .description( event.description )
                .end( event.end )
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
            "begindate" -> date,
            "beginhour" -> nonEmptyText (5, 5),
            "enddate" -> date,
            "endhour" -> nonEmptyText (5, 5),
            "location" -> text,
            "description" -> text,
            "tags" -> text
        )( ( title, begindate, beginhour, endate, endhour, location, description, tags ) => (
            new EventBuilder()
                .title( title )
                .location( location )
                .description( description )
                .begin( new DateTime( begindate ).plusHours( beginhour.split( ":" )(0).toInt ).plusMinutes( beginhour.split( ":" )(1).toInt ) )
                .end( new DateTime( endate ).plusHours( endhour.split( ":" )(0).toInt ).plusMinutes( endhour.split( ":" )(1).toInt ) )
                .tags( cleanTags( tags ) )
                .toEvent )
        )
         ( ( event: Event ) => Some( ( event.title, event.begin.toDate, "12345", event.end.toDate, "12345", event.location, event.description, event.tags.mkString( "," ) ) ) )
    )

    private def cleanTags( tags: String ): List[ String ] = {
        tags.trim().toUpperCase.split(",").toList
    }
}