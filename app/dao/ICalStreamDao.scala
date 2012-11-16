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

package dao

import com.mongodb.casbah.Imports._
import configuration.injection.MongoConnectionPool
import configuration.injection.MongoProp._
import fr.scala.util.collection.CollectionsUtils
import models.{ICalStreamTypeClass, ICalStream}

object ICalStreamDao extends CollectionsUtils
        with MongoOperations
        with ICalStreamTypeClass
        with MongoConnectionPool {

    def findAll()(implicit dbName: MongoDbName): List[ICalStream] = find[ICalStream](MongoDBObject())

    def saveICalStream(stream: ICalStream)(implicit dbName: MongoDbName) = save(stream)
}