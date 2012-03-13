package controllers

import play.api.mvc._
import service.{CalendarStream, ICalBuilder}
import models.Event

object Application extends Controller {

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def flux = Action {
        renderEvents(new CalendarStream().stubEvents)
    }

    def searchFlux( search: String ) = Action {
        val tags: List[String] = search.split(" ").toList
        //EventDao.find(tags) with ProdParameterMongoInjection
        renderEvents( new CalendarStream().search( tags ) )
    }

    private def renderEvents( events: List[ Event ] ) = {
        Ok( calendarService.buildCalendar( events ) ).as( "text/calendar" )
    }

}