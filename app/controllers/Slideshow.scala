package controllers

import com.codahale.jerkson.Json
import play.api._
import play.api.mvc._
import dao.EventDao
import org.joda.time.DateTime

object Slideshow extends OneCalendarController with Json {

    def slideshow()(implicit now: () => Long = () => DateTime.now.getMillis) = Action { request =>
            request.headers.get("Content-Type") match {
                case Some("application/ajax") => Ok(generate(EventDao.closestEvents())).as("application/json")
                case _ => Ok(views.html.slideshow())
            }

    }
}
