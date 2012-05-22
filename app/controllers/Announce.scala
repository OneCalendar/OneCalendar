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

import play.api.mvc._
import dao.configuration.injection.MongoConfiguration
import service.LoadICalStream

object Announce extends Controller {

    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")

    def agilefrance = Action {
        Ok( views.html.agilefrance() )
    }

    def loadAgileFranceCalendar = Action {
        val url: String = "http://10.61.32.155:8080/ical.ics"

        val iCalService: LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, "AGILEFRANCE" )

        Ok( "base " + mongoConfigProd.dbName + " loaded with agile france Calendar" )
    }
}