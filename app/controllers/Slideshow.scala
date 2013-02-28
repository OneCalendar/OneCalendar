package controllers

import com.codahale.jerkson.Json
import play.api._
import play.api.mvc._
import dao.EventDao
import org.joda.time.DateTime
import io.Source._
import models.Event

object Slideshow extends OneCalendarController with Json {

    def slideshow()(implicit now: () => Long = () => DateTime.now.getMillis) = Action { request =>
            request.headers.get("Content-Type") match {
                case Some("application/ajax") => Ok(generate(EventDao.closestEvents())).as("application/json")
                case _ => Ok(views.html.slideshow())
            }

    }

  def devoxxshow()(implicit now: () => Long = () => DateTime.now.getMillis) = Action { request =>

      val content = """[{
                            "uid":"5475776194",
                            "title":"LES TESTS: POURQUOI ET COMMENT ?",
                            "begin":1367240400000,
                            "end":1367355600000,
                            "location":" Mile Davis C",
                            "description":"description",
                            "tags":[
                               "SCALA",
                               "JAVA",
                               "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                         },
                      {
                           "uid":"5475776194",
                           "title":"ANIMEZ VOS PAGES HTML5: UN TOUR D'HORIZON COMPLET DES TECHNIQUES D'ANIMATION EN HTML5",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" Mile Davis C",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"7 IDÉES POUR TRANSFORMER UNE BONNE ÉQUIPE EN ÉQUIPE EXCEPTIONNELLE.",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" La seine A",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"STRUCTURES DE DONNÉES EXOTIQUES, AU DELÀ DE ARRAYLIST, HASHMAP ET AUTRES HASHSET.",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" Auditorium",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"THE WEARABLE APPLICATION SERVER AND OTHER ADVENTURES IN SOFTWARE ENGINEERING",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":"E. Fitzgerald & L. Armstrong",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"LES TESTS: POURQUOI ET COMMENT ?",
                           "begin":1367240400000,
                           "end":1367556000000,
                           "location":" Mile Davis C",
                           "description":"description",
                           "tags":[
                            "SCALA",
                            "JAVA",
                            "DEVOXX"
                            ],
                           "originalStream":"eventbrite-scala",
                           "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"ANIMEZ VOS PAGES HTML5: UN TOUR D'HORIZON COMPLET DES TECHNIQUES D'ANIMATION EN HTML5",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" Mile Davis C",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"7 IDÉES POUR TRANSFORMER UNE BONNE ÉQUIPE EN ÉQUIPE EXCEPTIONNELLE.",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" La seine A",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"STRUCTURES DE DONNÉES EXOTIQUES, AU DELÀ DE ARRAYLIST, HASHMAP ET AUTRES HASHSET.",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":" Auditorium",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      },
                      {
                           "uid":"5475776194",
                           "title":"THE WEARABLE APPLICATION SERVER AND OTHER ADVENTURES IN SOFTWARE ENGINEERING",
                           "begin":1367240400000,
                           "end":1367355600000,
                           "location":"E. Fitzgerald & L. Armstrong",
                           "description":"description",
                           "tags":[
                             "SCALA",
                             "JAVA",
                             "DEVOXX"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                      } 
                      ]"""

      request.headers.get("Content-Type") match {
        //case Some("application/ajax") => Ok(generate(EventDao.closestEvents(tags = List("devoxx"))).as("application/json")
        case Some("application/ajax") => Ok(content).as("application/json")
        case _ => Ok(views.html.devoxxshow())
      }

    }
}
