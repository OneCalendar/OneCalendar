package dao

import configuration.injection.MongoConfiguration
import models._
import collection.JavaConversions
import org.joda.time.DateTime
import com.mongodb._
import java.util.ArrayList

object EventDao {

    private val PREVIEW_SIZE = 3
    private val mongo: Mongo = new Mongo()

    def deleteAll()(implicit dbConfig: MongoConfiguration) {
        getEventsCollection(dbConfig.dbName).drop()
    }

    def saveEvent(event: Event)(implicit dbConfig: MongoConfiguration) {
        val bObject: BasicDBObject = fromEvent2DBObject(event)
        getEventsCollection(dbConfig.dbName).save(bObject)
    }

    def findByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): List[Event] = {
        val eventCollection: DBCollection = getEventsCollection(dbConfig.dbName)
        //db.events.find({"tags" : "java"})

        val javaTags: java.util.List[String] = toArrayList(tags)

        val query: DBObject = new QueryBuilder().put("tags").in(javaTags).get
        //println("TEST QUERY: " + query.toString)    //TODO use logback play logger

        val cursor: DBCursor = eventCollection.find(query)

        dbCursorToEvents(cursor)
    }

    def findPreviewByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): SearchPreview = {
        val eventCollection: DBCollection = getEventsCollection(dbConfig.dbName)
        val javaTags: java.util.List[String] = toArrayList(tags)
        val query: DBObject = new QueryBuilder().put("tags").in(javaTags).get
        val count = eventCollection.count(query)
        val cursor: DBCursor = eventCollection.find(query).limit(PREVIEW_SIZE)
        val events: List[Event] = dbCursorToEvents(cursor)

        SearchPreview(count, events)
    }

    def findAll()(implicit dbConfig: MongoConfiguration): List[Event] = {
        val cursor: DBCursor = getEventsCollection(dbConfig.dbName).find()

        dbCursorToEvents(cursor)
    }

    private def getEventsCollection(dbName: String): DBCollection = {
        val db: DB = getDatabase(dbName)
        db.getCollection("events")
    }

    private def getDatabase(dbname: String): DB = {
        val db: DB = mongo.getDB(dbname)
        db.requestStart()
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