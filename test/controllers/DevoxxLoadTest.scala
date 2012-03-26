/*
 * This file is part of OneCalendar.
 *
 * OneCalendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OneCalendar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
 */

package controllers

import org.scalatest.FunSuite
import com.codahale.jerkson.Json
import org.codehaus.jackson.annotate._
import play.api.mvc.Action._
import org.scalatest.matchers.ShouldMatchers


case class DevoxxTag(name: String) {}

case class DevoxxSpeakers(speakerUri: String, speaker: String) {}

@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxPresentation(tags: Seq[DevoxxTag], summary: String, id: Long,
                              speakerUri: String, title: String, speaker: String,
                              track: String, experience: String, speakers: Seq[DevoxxSpeakers],
                              room: String) {}


@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxSchedule(id: Long, partnerSlot: Boolean, fromTime: String, code: String, note: String, toTime: String, kind: String, room: String
                          //                           ,presentationUri:String="",speaker:String="", title:String="", speakerUri:String=""
                             ) {
    //    def this(id:Long,partnerSlot:Boolean, fromTime:String, code:String,note:String, toTime:String,kind:String, room:String) =
    //        this(id,partnerSlot,fromTime,code,toTime,room)
    //
    //    def this(presentationUri:String,speaker:String,code:String, fromTime:String, partnerSlot:Boolean, toTime:String,id:Long, kind:String, title:String, speakerUri:String, room:String) =
    //        this(presentationUri,speaker,code,fromTime,partnerSlot,toTime,id,kind,title,speakerUri,room)

}

@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxSchedule2() {}

class DevoxxLoadTest extends FunSuite with Json with ShouldMatchers {


    def getRestContent(url: String): String = {
        import org.apache.http.impl.client._
        import org.apache.http.client.methods._
        val httpClient = new DefaultHttpClient()
        val httpResponse = httpClient.execute(new HttpGet(url))
        val entity = httpResponse.getEntity()
        var content = ""
        if (entity != null) {
            val inputStream = entity.getContent()
            content = io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
        }
        httpClient.getConnectionManager().shutdown()
        return content
    }

    test("load rest url presentation") {
        var contentPresentation: String = getRestContent("https://cfp.devoxx.com/rest/v1/events/6/presentations")
        println(contentPresentation)
    }


    test("load rest url schedule") {
        var contentPresentation: String = getRestContent("https://cfp.devoxx.com/rest/v1/events/6/schedule")
        println(contentPresentation)
    }

    test("load json of devoxx") {
        val devoxxJson: String = """{
            "tags":[{"name":"DevOps"},{"name":"Versioning"}],
            "summary":"Nous sommes amenÃ©s quotidiennement Ã  modifier et partager du code:\r\n\r\n    * refactoring et tests afin d\u2019en amÃ©liorer la qualitÃ©,\r\n    * versioning afin de conserver et tracer ces changements.\r\n\r\nQu\u2019en est-il cÃ´tÃ© base de donnÃ©es ? En Ãªtes-vous encore Ã  envoyer vos scripts SQL directement Ã  l\u2019exploitation ? Les avez-vous testÃ©s ?\r\n\r\nDirectement inspirÃ© du livre \u201cRefactoring Databases: Evolutionary Database Design\u201d, Liquibase est un outil open-source qui permet de rÃ©pondre Ã  ces besoins essentiels.\r\n\r\nCette prÃ©sentation se veut Ãªtre une dÃ©monstration des forces de cet outil, de la crÃ©ation de structures de donnÃ©es Ã  leur livraison sur plusieurs environnements en passant par les tests.",
            "id":1409,
            "speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1614",
            "title":"(R)Ã©volutionnez vos bases de donnÃ©es avec Liquibase !",
            "speaker":"Florent Biville","track":"Entreprises et pratiques",
            "experience":"NOVICE",
            "speakers":[{"speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1614","speaker":"Florent Biville"}],
            "type":"Quickie","room":"La Seine C"
            }
            """

        val devoxxEvent: DevoxxPresentation = parse[DevoxxPresentation](devoxxJson)
        println("experience=" + devoxxEvent.experience);
        println("id=" + devoxxEvent.id);
        println("speaker=" + devoxxEvent.speaker);
        println("speakerUri=" + devoxxEvent.speakerUri);
        println("summary=" + devoxxEvent.summary);
        println("title=" + devoxxEvent.title);
        println("track=" + devoxxEvent.track);
        println("experience=" + devoxxEvent.experience);
        println("room=" + devoxxEvent.room);

    }




    test("load json devoxx schedule short") {
        parse[DevoxxSchedule]("""
        {"id":731,"partnerSlot":false,"fromTime":"2012-04-18 08:00:00.0","code":"DVFR12_REG_1",
        "type":"Conference","note":"Registration","toTime":"2012-04-18 09:30:00.0","kind":"Registration","room":"Hall d'exposition"},
        """)

    }
    //
    //    test ("load json devox schedule long") {
    //        var devoxxSchedule: DevoxxSchedule = parse[DevoxxSchedule]("""
    //        {"speaker":"Jos[0xc3][0xa9] Paumard","presentationUri":"http://cfp.devoxx.com/rest/v1/events/presentations/1134",
    //        "code":"DVFR12_U_18_A_1","fromTime":"2012-04-18 09:30:00.0","partnerSlot":false,"type":"University",
    //        "speakers":[{"speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1420","speaker":"Jos[0xc3][0xa9] Paumard"}],
    //        "kind":"Talk","toTime":"2012-04-18 12:30:00.0",
    //        "id":565,"title":"De Runnable et synchronized [0xc3][0xa0] parallel() et atomically()",
    //        "speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1420","room":"La Seine A"}
    //        """)
    //        devoxxSchedule.id should be (565)
    //    }
}