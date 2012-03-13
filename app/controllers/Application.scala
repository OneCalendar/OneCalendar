package controllers

import play.api.mvc._
import service.{CalendarStream, ICalBuilder}
import models.Event
import dao.configuration.injection.MongoConfiguration
import dao.EventDao

object Application extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration( "OneCalendar" )

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def flux = Action {
        renderEvents(new CalendarStream().stubEvents)
    }

    def findByTags( keyWords: String ) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents( EventDao.findByTag( tags ) )
    }

    private def renderEvents( events: List[ Event ] ) = {
        Ok( calendarService.buildCalendar( events ) ).as( "text/calendar" )
    }

}