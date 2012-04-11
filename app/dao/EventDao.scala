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
import collection.JavaConversions
import com.mongodb._
import play.api.Logger
import org.joda.time.DateTime
import java.util.ArrayList

object EventDao {

    private val log = Logger( "EventDao" )
    
    def listTags()(implicit dbConfig: MongoConfiguration): List[String] = {
        val collection: DBCollection = getEventsCollection(dbConfig.dbName)
        val map = "function(){" +
            "    this.tags.forEach(" +
            "        function(z){" +
            "            emit( z , { count : 1 } );" +
            "        }" +
            "    );" +
            "};"
        val reduce = "function( key , values ){" +
            "    var total = 0;" +
            "    for ( var i=0; i<values.length; i++ )" +
            "        total += values[i].count;" +
            "    return { count : total };" +
            "};"
        collection.mapReduce(map, reduce, "tagcloud", BasicDBObjectBuilder.start().get())
        val tagcloud: DBCollection = collection.getDB.getCollection("tagcloud")
        val sort: DBObject = BasicDBObjectBuilder.start()
            .add("_id", "1")
            .get()
        val cursor: DBCursor = tagcloud.find().sort(sort)

        var tags: List[String] = List() //TODO refactor to use immutable list in val

        while (cursor.hasNext) {
            val dbObject: DBObject = cursor.next
            val tag: String = dbObject.toMap.get("_id").asInstanceOf[String]
            tags = tags :+ tag
        }
        tags
    }


    private val PREVIEW_SIZE = 3
    private val mongoURI: MongoURI = {
        val m = new MongoURI("mongodb://127.0.0.1")
        m.getOptions.connectionsPerHost = 100
        m
    }

    private val mongo: Mongo = (new Mongo.Holder()).connect( mongoURI )
    
    def deleteAll()(implicit dbConfig: MongoConfiguration) {
        getEventsCollection(dbConfig.dbName).drop()
    }

    def saveEvent(event: Event)(implicit dbConfig: MongoConfiguration) {
        val bObject: BasicDBObject = fromEvent2DBObject(event)
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

    private def getEventsCollection( dbName: String ): DBCollection = getDatabase( dbName ).getCollection( "events" )

    private def getDatabase( dbname: String ): DB = {
        val db: DB = mongo.getDB( dbname )
        db.requestStart
        db
    }

    private def fromDbObject2Event(one: DBObject): Event = {
        val tags: BasicDBList = one.toMap.get("tags").asInstanceOf[BasicDBList]
        val scalaTags: List[String] = tags.toArray.toList.map(_.asInstanceOf[String])

        val event: Event = new Event(
            one.toMap.get("uid").asInstanceOf[String],
            one.toMap.get("title").asInstanceOf[String],
            new DateTime(one.toMap.get("begin").asInstanceOf[Long]),
            new DateTime(one.toMap.get("end").asInstanceOf[Long]),
            one.toMap.get("location").asInstanceOf[String],
            one.toMap.get("description").asInstanceOf[String],
            scalaTags
        )
        event
    }

    private def fromEvent2DBObject(event: Event): BasicDBObject = {
        val bObject: BasicDBObject = new BasicDBObject()
        bObject.put("uid", event.uid)
        bObject.put("title", event.title)
        bObject.put("begin", event.begin.toDate.getTime)
        bObject.put("end", event.end.toDate.getTime)
        bObject.put("location", event.location)
        bObject.put("description", event.description)
        bObject.put("tags", JavaConversions.asJavaCollection(event.tags))
        bObject
    }

    private def toArrayList(tags: List[String]): java.util.List[String] = {
        val javaTags: java.util.List[String] = new ArrayList[String]()
        tags.foreach(tag => javaTags.add(tag.toUpperCase))

        javaTags
    }

    private def dbCursorToEvents(cursor: DBCursor): List[Event] = {
        var events: List[Event] = List() //TODO refactor to use immutable list in val

        while (cursor.hasNext) {
            val dbObject: DBObject = cursor.next
            val event: Event = fromDbObject2Event(dbObject)
            events = events :+ event
        }

        events
    }
}