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

import play.api.mvc._
import dao.configuration.injection.MongoConfiguration
import service.LoadICalStream

object Announce extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    // TODO dead code
    def agilefrance = Action {
        Ok( views.html.agilefrance() )
    }

    // TODO dead code
    def loadAgileFranceCalendar = Action {
        val url: String = "http://10.61.32.155:8080/agilefrance.ics"

        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, "AGILEFRANCE" )

        Ok( "base " + mongoConfigProd.dbName + " loaded with agile france Calendar" )
    }
}