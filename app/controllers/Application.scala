package controllers

import _root_.service.CalendarStream
import play.api._
import play.api.mvc._
import service.CalendarStream

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def flux = Action {
    Ok(new CalendarStream().fetch).as("text/calendar")
  }

}