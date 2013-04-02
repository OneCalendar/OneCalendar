/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import org.scalatest.FunSuite
import com.codahale.jerkson.Json
import org.scalatest.matchers.ShouldMatchers
import models._
import service.LoadDevoxx

class DevoxxLoadTest extends FunSuite with Json with ShouldMatchers {

    /*test("load rest url presentation") {
        val presentations: Seq[DevoxxPresentation] = LoadDevoxx.parseUrl[Seq[DevoxxPresentation]]("https://cfp.devoxx.com/rest/v1/events/7/presentations")
        presentations.size should be > 0
    }

    test("load rest url events") {
      val beans: Seq[DevoxxEvents] = LoadDevoxx.parseUrl[Seq[DevoxxEvents]]("https://cfp.devoxx.com/rest/v1/events/")
      beans.size should be > 0
    }

    test("load rest url schedule") {
        val schedules: Seq[DevoxxSchedule] = LoadDevoxx.parseUrl[Seq[DevoxxSchedule]]("https://cfp.devoxx.com/rest/v1/events/7/schedule")
        schedules.size should be > 0
    }*/

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
        devoxxEvent.experience should be("NOVICE")
        devoxxEvent.id should be(1409)
        devoxxEvent.speaker should be("Florent Biville")
        devoxxEvent.speakerUri should be("http://cfp.devoxx.com/rest/v1/events/speakers/1614")
        devoxxEvent.title should be("(R)Ã©volutionnez vos bases de donnÃ©es avec Liquibase !")
        devoxxEvent.track should be("Entreprises et pratiques")
        devoxxEvent.room.get should be("La Seine C")
    }

    test("load json devoxx schedule short") {
        parse[DevoxxSchedule]("""
        {"id":731,"partnerSlot":false,"fromTime":"2012-04-18 08:00:00.0","code":"DVFR12_REG_1",
        "type":"Conference","note":"Registration","toTime":"2012-04-18 09:30:00.0","kind":"Registration","room":"Hall d'exposition"},
        """)

    }

    test("load one presention") {
        parse[DevoxxPresentation]("""
    {"tags":[{"name":"Xtext"},{"name":"IzPack"},{"name":"DSL"},
    {"name":"Eclipse"}],"summary":"Cette présentation permettra de découvrir la technologie Xtext et sera décomposée en plusieurs parties:\r\n\r\n1) une première partie, plutôt théorique, sur les différentes parties de Xtext et leur rôle bien précis\r\n2) une seconde, plus business, sur des utilisations actuelles de cette technologie\r\n3) enfin,une dernière, plus pratique, montrera un cas d'utilisation de Xtext pour développer un éditeur IzPack et le lien avec les outils de développement Java",
    "id":1415,"speakerUri":"http://cfp2.devoxx.com/rest/v1/events/speakers/1571",
    "title":"Dessine moi un language: à la découverte de Xtext","speaker":"Jeff Maury",
    "track":"Languages alternatifs","experience":"NOVICE","speakers":[{"speakerUri":"http://cfp2.devoxx.com/rest/v1/events/speakers/1571","speaker":"Jeff Maury"}],
    "type":"Tools in Action","room":"La Seine A"}
    """)
    }

    test("load json devox schedule long") {
        val devoxxSchedule: DevoxxSchedule = parse[DevoxxSchedule]("""
            {"speaker":"Jos[0xc3][0xa9] Paumard","presentationUri":"http://cfp.devoxx.com/rest/v1/events/presentations/1134",
            "code":"DVFR12_U_18_A_1","fromTime":"2012-04-18 09:30:00.0","partnerSlot":false,"type":"University",
            "speakers":[{"speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1420","speaker":"Jos[0xc3][0xa9] Paumard"}],
            "kind":"Talk","toTime":"2012-04-18 12:30:00.0",
            "id":565,"title":"De Runnable et synchronized [0xc3][0xa0] parallel() et atomically()",
            "speakerUri":"http://cfp.devoxx.com/rest/v1/events/speakers/1420","room":"La Seine A"}
            """)
        devoxxSchedule.id.get should be(565)
        devoxxSchedule.speakerUri.get should be("http://cfp.devoxx.com/rest/v1/events/speakers/1420")
    }
}