package controllers

import play.api._
import play.api.mvc._
import service.{CalendarStream, ICalBuilder}

object Application extends Controller {

  val calendarService: ICalBuilder = new ICalBuilder()

  def index = Action {
    Ok( views.html.index() )
  }

  def flux = Action {
    Ok( calendarService.buildCalendar( new CalendarStream().stubEvents ) ).as( "text/calendar" )
  }

}