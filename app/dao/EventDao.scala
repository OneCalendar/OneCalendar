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
import configuration.injection.MongoProp.MongoDbName
import configuration.injection.MongoProp.MongoDbPort
import fr.scala.util.collection.CollectionsUtils
import models._
import play.api.Logger
import models.SearchPreview

object EventDao extends CollectionsUtils
        with EventDaoTrait
        with MongoOperations
        with EventTypeClass
        with MongoConnectionPool {

    private val log = Logger("EventDao")

    private val PREVIEW_SIZE = 3

    def deleteByOriginalStream(originalStream: String)(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long) = {
        delete(DBObject("originalStream" -> originalStream))
    }

    def saveEvent(event: Event)(implicit dbName: MongoDbName, port: MongoDbPort = 27017) = save(event)

    def findByTag(tags: List[String])(implicit dbName: MongoDbName, port: MongoDbPort = 27017): List[Event] = {
        val query = "tags" $in tags.map(_.toUpperCase)
        log.debug("query find by tag %s".format(query))
        find[Event](query)
    }

    def findPreviewByTag(tags: List[String])(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): SearchPreview = {
        val query = ( "tags" $in tags.map(_.toUpperCase) ) ++ ( "begin" $gt now() )
        val c = count[Event](query)

        val sortByBeginDate = MongoDBObject("begin" -> 1)

        SearchPreview(c, find[Event](query, sortByBeginDate, PREVIEW_SIZE))
    }

    def findAll()(implicit dbName: MongoDbName, port: MongoDbPort = 27017): List[Event] = find[Event](MongoDBObject())

    def findAllFromNow()(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long) = {
        val query = "begin" $gt now()
        find[Event](query)
    }

    def listTags()(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): List[String] = {
        val query = "begin" $gt now()
        retrieveMongoCollection(EventMongoModel.collectionName).distinct("tags", query).toList.asInstanceOf[List[String]]
    }

    def countFutureEvents()(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): Long = {
        val query = "begin" $gt now()
        count(query)
    }

    /**
     * Find events that has not already finished without almost those
     * Or
     * Find events that will begin pretty soon
     * >                 Now
     * >            offset  afterset
     * Time
     * >-------------|xxxx$xxx|-------->
     */
    def closestEvents(offset: Int = 5, afterset : Int = 2, tags:List[String]= List.empty)
                     (implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): List[Event] = {
        import scala.concurrent.duration._

        val offsetMillis: Long = (offset minutes).toMillis
        val aftersetMillis: Long = (afterset hours).toMillis
        var futurEvents : DBObject =  null
        var eventsBeganNotFinish = DBList.empty

        if (!tags.isEmpty) {
           futurEvents = ("tags" $in tags.map(_.toUpperCase)) ++ ("begin" $gte (now() - offsetMillis) $lte (now() + aftersetMillis))
           eventsBeganNotFinish = DBList("tags" $in tags.map(_.toUpperCase), "begin" $lte now(), "end" $gt (now() - offsetMillis))

        } else{

          futurEvents = ("begin" $gte (now() - offsetMillis) $lte (now() + aftersetMillis) )
          eventsBeganNotFinish = DBList("begin" $lte now(), "end" $gt (now() - offsetMillis) )
        }
        val query =  DBObject("$or" -> DBList(futurEvents,DBObject("$and" -> eventsBeganNotFinish)))

        find[Event](query)
    }
}