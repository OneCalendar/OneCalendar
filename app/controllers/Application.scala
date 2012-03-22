package controllers

import play.api.mvc._
import models._
import dao.configuration.injection.MongoConfiguration
import dao.EventDao
import service.{LoadICalStream, ICalBuilder}
import play.api.libs.json._

object Application extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    val calendarService: ICalBuilder = new ICalBuilder()

    def index = Action {
        Ok(views.html.index())
    }

    def findByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        renderEvents(EventDao.findByTag(tags))
    }

    def findPreviewByTags(keyWords: String) = Action {
        val tags: List[String] = keyWords.split(" ").toList
        val previewEvents: SearchPreview = EventDao.findPreviewByTag(tags)
        Ok(Json.toJson(renderPreviewEventInJson(previewEvents)))
    }

    def loadDevoxxCalendar = Action {
        val url: String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad(url, "DEVOXX")
        Ok("base " + mongoConfigProd.dbName + " loaded with devoxx Calendar")
    }

    def about = Action {
        Ok( views.html.about() )
    }
    
    def fetchCloudOfTags = Action {
        Ok(Json.toJson(EventDao.listTags())).as("application/json")
    }
    
    private def renderEvents( events: List[ Event ] ) = {
        events match {
            case Nil => NotFound("Aucun évènement pour la recherche")
            case _ => Ok(calendarService.buildCalendar(events)).as("text/calendar")
        }
    }

    private def renderPreviewEventInJson(previewEvents: SearchPreview): JsValue = {
        JsObject(
            List(
                ("size", JsNumber(previewEvents.size)),
                ("eventList", JsArray(
                    List(
                        JsObject(List(
                            ("event", JsObject(List(
                                ("date", JsString(previewEvents.events(0).begin.toString)),
                                ("title", JsString(previewEvents.events(0).title)),
                                ("location", JsString(previewEvents.events(0).location))
                            ))))),
                        JsObject(List(
                            ("event", JsObject(List(
                                ("date", JsString(previewEvents.events(1).begin.toString)),
                                ("title", JsString(previewEvents.events(1).title)),
                                ("location", JsString(previewEvents.events(1).location))
                            ))))),
                        JsObject(List(
                            ("event", JsObject(List(
                                ("date", JsString(previewEvents.events(2).begin.toString)),
                                ("title", JsString(previewEvents.events(2).title)),
                                ("location", JsString(previewEvents.events(2).location))
                            )))))
                    ))
                    )
            )
        )
    }
}