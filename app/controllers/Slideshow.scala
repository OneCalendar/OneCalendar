package controllers

import com.codahale.jerkson.Json
import play.api._
import play.api.mvc._
import dao.EventDao
import org.joda.time.DateTime
import io.Source._

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
                            "title":"Typesafe's Fast Track to Scala",
                            "begin":1367240400000,
                            "end":1367355600000,
                            "location":" New York Information Technology Centre 55 Broad Street New York NY 10004-250 NY United States",
                            "description":"\r\n\r\nThis two-day Fast Track to Scala course will give you an excellent grounding in Scala.\r\nThis course is intended to enable developers or development managers, who are experienced programmers in Java or other production languages like C++, C# or Ruby, to confidently start programming in Scala. No previous knowledge of Scala is assumed.\r\nAlthough intense, the course ensures you will have a solid understanding of the fundamentals of the language, the tooling and the development process as well as a good appreciation of the more advanced features. If you already have Scala programming experience, then this course could be a useful refresher.\r\n\r\n\r\n \r\nLEARN HOW TO:\r\n\r\n\r\nbe a competent user of Scala constructs in application code\r\nknow and be able to apply the functional programming style in Scala\r\nknow how to use the fundamental Scala tools\r\nbe confident to start using Scala in production applications\r\n\r\n\r\n \r\nPROGRAMME:\r\n\r\n\r\nIntroduction\r\nFirst steps in Scala\r\nOO basics\r\nTesting\r\nCollections and functional programming\r\nFor-expressions and loops\r\nInheritance and traits\r\nPattern Matching\r\nDealing with optional values\r\n\r\n\r\n\r\nTHE EXPERT\r\n\r\n\r\n\r\n  \r\n\r\nBrendan McAdams is a polyglot programmer, who has spent much of his career in the financial sector – including building trading systems – and harbors a not-so-secret fascination with distributed and durable system architectures such as Akka. In his last role he served as the MongoDB Scala evangelist for 10gen; he also developed Casbah, the MongoDB driver for Scala, and architected the integration layer for MongoDB and Hadoop. Brendan now works within Typesafe’s Professional Services team helping Scala, Akka, and Play users better understand and deploy the Typesafe products.\r\nWatch Brendan's podcasts here.\r\n\r\n\r\n\r\n\r\n \r\nIS THIS COURSE FOR YOU?\r\n\r\nThis Fast Track to Scala workshop is intended to enable developers or development managers, who are experienced programmers in Java or other production languages like C++, C# or Ruby, to confidently start programming in Scala. No previous knowledge of Scala is assumed.\r\nIf you already have Scala programming experience, then this course could be a useful refresher.\r\n \r\n\r\nCOURSE PREREQUISITES\r\n\r\nImportant - Please note: Delegates are requested to bring their own laptop for this course, with Java 6 installed. If you are unable to bring a laptop for the course, please contact the sales team on ++1 347 708 1529, or email sales@skillsmatter.com. A full installation guide for the course software will be provided with your course joining instructions.\r\n\r\n \r\nCOURSE LABS & EXERCISES\r\n\r\nThe presentation of this Fast Track to Scala course will frequently be mixed with hands-on exercises that give you a good opportunity to try what you have learnt and a chance to clarify your understanding.\r\n \r\n \r\nTERMS AND CONDITIONS\r\nClick here to read our Terms & Conditions",
                            "tags":[
                               "SCALA"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5475776194/SRCH"
                         },
                         {
                            "uid":"5474739092",
                            "title":"Typesafe's Advanced Scala",
                            "begin":1367413200000,
                            "end":1367528400000,
                            "location":" New York Information Technology Centre 55 Broad Street New York NY 10004-250 NY United States",
                            "description":"If you already have some programming experience with Scala and need to understand it's advanced features, Typesafe's Advanced Scala will show you how to unleash the full power of this scalable language.\r\nIt is intended to enable developers, who have at least 3 months programming experience with Scala and feel comfortable to use it in their applications, to fully understand various advanced features of this programming language and how to apply these to create well designed libraries or DSLs using proven practices. Basic knowledge of Scala like covered in Typesafe's Fast Track to Scala course is a prerequisite to understand topics like advanced functional programming, details of the type system, implicits, etc\r\n \r\nLEARN HOW TO\r\n\r\nAfter having participated in this course you should\r\n\r\nunderstand all aspects of the object-functional approach\r\nknow and be able to apply advanced features of Scala's type system\r\nfully understand implicit conversions\r\nbe confident to design libraries and DSLs\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n \r\n\r\n \r\n\r\n\r\n\r\nTHE EXPERT\r\n\r\n\r\n\r\n\r\n\r\nBrendan McAdams is a polyglot programmer, who has spent much of his career in the financial sector – including building trading systems – and harbors a not-so-secret fascination with distributed and durable system architectures such as Akka. In his last role he served as the MongoDB Scala evangelist for 10gen; he also developed Casbah, the MongoDB driver for Scala, and architected the integration layer for MongoDB and Hadoop. Brendan now works within Typesafe’s Professional Services team helping Scala, Akka, and Play users better understand and deploy the Typesafe products.\r\nWatch Brendan's podcasts here.\r\n\r\n\r\n\r\n\r\n \r\nPROGRAMME\r\n\r\n\r\n\r\n\r\nShort recap of important basics\r\nObject-functional programming in depth\r\nMastering the type system\r\nExplicitly implicit\r\nInternal DSLs\r\nCustom Scala collections\r\n\r\n\r\n\r\n \r\nIS THIS COURSE FOR YOU?\r\n\r\nIf you are a developer with at least 3 months Scala programming experience and you would like to learn various advanced features of this programming language and how to apply these to create well designed libraries or DSLs using proven practices, Typesafe's Advanced Scala is for you!\r\n\r\n \r\nCOURSE PREREQUISITES\r\n\r\nIf you have at least 3 months programming experience with Scala and hence have basic knowledge of Scala covered in Typesafe's Fast Track to Scala course course is a prerequisite to understand topics like advanced functional programming, details of the type system, implicits, etc\r\nImportant - Please note: Delegates are requested to bring their own laptop for this course. If you are unable to bring a laptop for the course, please contact the sales team on +1 347 708 1529, or by email bookings. A full installation guide for the course software will be provided with your course joining instructions.\r\n\r\n \r\nCOURSE LABS & EXERCISES\r\n \r\nThis Advanced Scala course features lectures, mixed with frequent hands-on exercises that give you a good opportunity to try what you have learnt and a chance to clarify your understanding.\r\n \r\nTERMS AND CONDITIONS\r\nClick here to read our Terms & Conditions",
                            "tags":[
                               "SCALA"
                            ],
                            "originalStream":"eventbrite-scala",
                            "url":"http://www.eventbrite.com/event/5474739092/SRCH"
                         }]"""
        request.headers.get("Content-Type") match {
        case Some("application/ajax") => Ok(content).as("application/json")
        case _ => Ok(views.html.devoxxshow())
      }

    }
}
