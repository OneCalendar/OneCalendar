package dao

import models.Event
import org.joda.time.DateTime
import collection.immutable.List
import com.mongodb._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSuite}
import models.builder.EventBuilder


/**
 * User: amira
 * Date: 09/03/12
 */

class DaoModuleTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    /**
     * RUNNING MONGO SERVER BEFORE in target directory -
     * mongod --dbpath data/ --fork --logpath data/mongodb.log
     */

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB( "test" )
        db
    }
    
    before {
        db.requestStart
        db.getCollection( "events" ).drop
    }
    
    after {
        db.requestDone
    }
    
    test("connecting to mongodb test") {
        val db: DB = DaoModule.getDatabase("test")
        db.getName should be("test")
    }

    test("getting events collection") {
        val eventsCollection: DBCollection = DaoModule.getEventsCollection("test")
        eventsCollection.getName should be("events")
    }

    test("saving a new event") {
        val event: Event = new EventBuilder()
            .uid("1")
            .title("BOF")
            .begin(new DateTime(2012, 04, 19, 0, 0, 0, 0))
            .end(new DateTime(2012, 04, 19, 0, 0, 0, 0))
            .description("")
            .location("")
            .tags(List("java", "devoxx"))
        .toEvent
        DaoModule.saveEvent("test", event)

        val eventsCollection: DBCollection = DaoModule.getEventsCollection("test")
        eventsCollection.count should be (1)

        val one: DBObject = eventsCollection.findOne()
        DaoModule.fromDbObject2Event(one) should be(event)
    }
}
