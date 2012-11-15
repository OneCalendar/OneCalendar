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

import configuration.injection.MongoConnectionPool
import configuration.injection.MongoPool.MongoDbName
import models._
import dao.EventMap._
import com.mongodb._
import casbah.Imports._
import play.api.Logger
import fr.scala.util.collection.CollectionsUtils
import models.SearchPreview


@deprecated
object EventDaoBis extends MongoConnectionPool {
    def findAll()(implicit dbName: MongoDbName) = EventDao.findAll()

    def deleteByOriginalStream(url: String)(implicit now: () => Long, dbName: MongoDbName) = EventDao.deleteByOriginalStream(url)

    def saveEvent(event: Event)(implicit now: () => Long, dbName: MongoDbName) = EventDao.saveEvent(event)
}

object EventDao extends CollectionsUtils
        with EventDaoTrait
        with MongoOperations
        with MongoConnectionPool {

    private val log = Logger("EventDao")

    private val PREVIEW_SIZE = 3

    def deleteByOriginalStream(originalStream: String)(implicit dbName: MongoDbName, now: () => Long) = {
        val query = ( "end" $gt now() ) ++ ( "originalStream" -> originalStream )
        log.debug("query deleteByOriginalStreal %s".format(query))
        delete(query)
    }

    def saveEvent(event: Event)(implicit dbName: MongoDbName) = save(event)

    def findByTag(tags: List[String])(implicit dbName: MongoDbName): List[Event] = {
        val query = "tags" $in tags.map(_.toUpperCase)
        log.debug("query find by tag %s".format(query))
        find[Event](query)
    }

    def findPreviewByTag(tags: List[String])(implicit dbName: MongoDbName, now: () => Long): SearchPreview = {
        val query = ( "tags" $in tags.map(_.toUpperCase) ) ++ ( "begin" $gt now() )
        val c = count[Event](query)

        val sortByBeginDate = MongoDBObject("begin" -> 1)

        SearchPreview(c, find[Event](query, sortByBeginDate, PREVIEW_SIZE))
    }

    def findAll()(implicit dbName: MongoDbName): List[Event] = find[Event](MongoDBObject())

    def listTags()(implicit dbName: MongoDbName, now: () => Long): List[String] = {
        val query = "begin" $gt now()
        retrieveMongoCollection(EventMongoModel.collectionName).distinct("tags", query).toList.asInstanceOf[List[String]]
    }
}