package controllers

import play.api.mvc._
import models.Event
import dao.configuration.injection.MongoConfiguration
import dao.EventDao
import service.{LoadICalStream, CalendarStream, ICalBuilder}

object Application extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags( keyWords: String ) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def loadDevoxxCalendar = Action {
        val url: String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad(url)
        Ok("base " + mongoConfigProd.dbName + " loaded with devoxx Calendar")
    }

    private def renderEvents( events: List[ Event ] ) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok( calendarService.buildCalendar( events ) ).as( "text/calendar" )
        }
    }

}