package controllers

//import com.codahale.jerkson.Json
import play.api._
import play.api.mvc._
import dao.EventDao
import org.joda.time.DateTime
import io.Source._
import models.Event

object Slideshow extends OneCalendarController /*with Json*/ {

    def slideshow()(implicit now: () => Long = () => DateTime.now.getMillis) = Action { request =>
            request.headers.get("Content-Type") match {
                //case Some("application/ajax") => Ok(generate(EventDao.closestEvents())).as("application/json")
                case _ => Ok(views.html.slideshow())
            }

    }

  def devoxxshow()(implicit now: () => Long = () => DateTime.now.getMillis) = Action { request =>
      request.headers.get("Content-Type") match {
        //case Some("application/ajax") => Ok(generate(EventDao.closestEvents(tags = List("devoxx")))).as("application/json")
        case _ => Ok(views.html.devoxxshow())
      }

    }
}