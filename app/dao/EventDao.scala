package dao

import models.Event
import collection.JavaConversions
import org.joda.time.DateTime
import com.mongodb._

object EventDao {

    val mongo: Mongo = new Mongo()

    def getEventsCollection(dbName: String): DBCollection = {
        val db: DB = getDatabase(dbName)
        db.getCollection("events")
    }

    def getDatabase(dbname: String): DB = {
        val db: DB = mongo.getDB(dbname)
        db.requestStart()
        db
    }

    def fromDbObject2Event(one: DBObject): Event = {
        val tags: BasicDBList = one.toMap.get("tags").asInstanceOf[BasicDBList]
        // TODO collection doesn't automatically map
        val scalaTags = tags.toArray.toList.map(_.asInstanceOf[String])
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

    def saveEvent(dbName: String, event: Event) {
        val bObject: BasicDBObject = fromEvent2DBObject(event)

        getEventsCollection(dbName).save(bObject)

    }

    private def fromEvent2DBObject( event: Event ): BasicDBObject = {
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
}