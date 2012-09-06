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

package dao

import configuration.injection.MongoConfiguration
import models._
import builder.EventBuilder
import collection.JavaConversions
import com.mongodb._
import play.api.Logger
import org.joda.time.DateTime
import java.util.ArrayList
import fr.scala.util.collection.CollectionsUtils
import annotation.tailrec

object EventDao extends CollectionsUtils {


    private val log = Logger( "EventDao" )

    private val PREVIEW_SIZE = 3

    private val mongoURI: MongoURI = {
        val m = new MongoURI("mongodb://127.0.0.1")
        m.getOptions.connectionsPerHost = 100
        m
    }

    private val mongo: Mongo = (new Mongo.Holder()).connect( mongoURI )

    @deprecated("never use deleteAll, very dangerous",since="27 aout 2012")
    def deleteAll()(implicit dbConfig: MongoConfiguration) {
        getEventsCollection(dbConfig.dbName).drop()
    }

    def deleteByOriginalStream(s: String)(implicit dbConfig: MongoConfiguration) = {
        val query: DBObject = QueryBuilder.start.put("originalStream").is(s).and("end").greaterThan(dbConfig.now).get
        log.debug("query deleteByOriginalStreal %s".format(query.toString))
        getEventsCollection(dbConfig.dbName).findAndRemove(query)
    }

    def saveEvent(event: Event)(implicit dbConfig: MongoConfiguration) {
        val bObject = fromEvent2DBObject(event)
        getEventsCollection(dbConfig.dbName).save(bObject)
    }

    def findByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): List[Event] = {
        val javaTags: java.util.List[String] = toArrayList(tags)

        val query: DBObject = new QueryBuilder().put("tags").in(javaTags).get
        log.debug( "query find by tag %s".format( query.toString ) )
        log.debug( "pool connection number: %s".format( mongoURI.getOptions.connectionsPerHost ) )

        dbCursorToEvents(getEventsCollection(dbConfig.dbName).find(query))
    }

    def findPreviewByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): SearchPreview = {
        val eventCollection: DBCollection = getEventsCollection(dbConfig.dbName)
        val javaTags: java.util.List[String] = toArrayList(tags)
        val query: DBObject = new QueryBuilder()
            .put("tags").in(javaTags)
            .put("begin").greaterThan(dbConfig.now)
            .get
        val count = eventCollection.count(query)

        val sort: DBObject = BasicDBObjectBuilder.start()
            .add("begin", 1)
            .get()
        val cursor: DBCursor = eventCollection.find(query)
            .sort(sort)
            .limit(PREVIEW_SIZE)
        val events: List[Event] = dbCursorToEvents(cursor)

        SearchPreview(count, events)
    }

    def findAll()(implicit dbConfig: MongoConfiguration): List[Event] = {
        val cursor: DBCursor = getEventsCollection(dbConfig.dbName).find()

        dbCursorToEvents(cursor)
    }

    def listTags()(implicit dbConfig: MongoConfiguration): List[String] = {
        val query = BasicDBObjectBuilder
          .start()
          .add("begin",new BasicDBObject("$gt", new DateTime().getMillis)).get()

        getDatabase( dbConfig.dbName ).getCollection( "events" ).distinct("tags",query).toList.asInstanceOf[List[String]]
    }

    private def getEventsCollection( dbName: String ): DBCollection = getDatabase( dbName ).getCollection( "events" )


    private def getDatabase( dbname: String ): DB = {
        val db: DB = mongo.getDB( dbname )
        db.requestStart
        db
    }

    private def fromDbObject2Event(one: DBObject): Event = {
        val tags: BasicDBList = one.toMap.get("tags").asInstanceOf[BasicDBList]
        val scalaTags: List[String] = tags.toArray.toList.map(_.asInstanceOf[String])

        new EventBuilder()
            .uid( one.toMap.get("uid").asInstanceOf[String] )
            .title( one.toMap.get("title").asInstanceOf[String] )
            .tags( scalaTags )
            .description( one.toMap.get("description").asInstanceOf[String] )
            .location( one.toMap.get("location").asInstanceOf[String] )
            .originalStream( one.toMap.get("originalStream").asInstanceOf[String] )
            .begin( new DateTime(one.toMap.get("begin").asInstanceOf[Long]) )
            .end( new DateTime(one.toMap.get("end").asInstanceOf[Long]) )
            .toEvent
    }

    private def fromEvent2DBObject(event: Event): DBObject = {
        BasicDBObjectBuilder.start()
        .add("uid", event.uid)
        .add("title", event.title)
        .add("begin", event.begin.toDate.getTime)
        .add("end", event.end.toDate.getTime)
        .add("location", event.location)
        .add("description", event.description)
        .add("tags", JavaConversions.asJavaCollection(event.tags))
        .add("originalStream", event.originalStream)
        .get
    }

    private def toArrayList(tags: List[String]): java.util.List[String] = {
        val javaTags: java.util.List[String] = new ArrayList[String]()
        tags.foreach(tag => javaTags.add(tag.toUpperCase))

        javaTags
    }

    @tailrec
    private def dbFunCursor(cursor:DBCursor , res :List[Event] ) :List[Event] = {

      cursor.hasNext() match {
        case true => dbFunCursor(cursor,fromDbObject2Event(cursor.next()) :: res)
        case false => res.reverse
      }
    }

    private def dbCursorToEvents(cursor: DBCursor): List[Event] = {
      dbFunCursor(cursor,List())
    }
}