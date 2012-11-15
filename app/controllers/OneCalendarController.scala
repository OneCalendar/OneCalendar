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

import play.api.mvc.Controller
import dao.configuration.injection.MongoConfiguration
import com.mongodb.casbah._

object mongoConn {
    private val conn:MongoConnection = MongoConnection()
    def apply(dbName: String) = conn(dbName)
}

trait OneCalendarController extends Controller {
    @deprecated("a jarter après que tous les dao passe à type class ","15 nov 2012")
    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")
    implicit val mongoCollectionProvider : (String) => MongoCollection = (name: String) => mongoConn("OneCalendar")(name)
}