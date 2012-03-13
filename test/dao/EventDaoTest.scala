package dao

import models.Event
import org.joda.time.DateTime
import collection.immutable.List
import com.mongodb._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSuite}
import models.builder.EventBuilder

class EventDaoTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

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
        val db: DB = EventDao.getDatabase("test")
        db.getName should be("test")
    }

    test("getting events collection") {
        val eventsCollection: DBCollection = EventDao.getEventsCollection("test")
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
        EventDao.saveEvent("test", event)

        val eventsCollection: DBCollection = EventDao.getEventsCollection("test")
        eventsCollection.count should be (1)

        val one: DBObject = eventsCollection.findOne()
        EventDao.fromDbObject2Event(one) should be(event)
    }

    test( "should find event by tag 'devoxx'" ) {
        initData
        EventDao.findByTag( List( "devoxx" ), "test" ) should be ( List( eventDevoxx ) )
    }
    
    test( "should find events by tags 'devoxx' or 'java' " ) {
        initData
        EventDao.findByTag( List( "devoxx", "java" ), "test" ) should be ( List( eventDevoxx, eventJava ) )
    }

    val eventDevoxx: Event = new EventBuilder()
        .uid("1")
        .title("BOF")
        .begin(new DateTime(2012, 04, 19, 0, 0, 0, 0))
        .end(new DateTime(2012, 04, 19, 0, 0, 0, 0))
        .tags(List("devoxx"))
        .toEvent

    val eventJava: Event = new EventBuilder()
        .uid("2")
        .title("BOF")
        .begin(new DateTime(2012, 04, 19, 0, 0, 0, 0))
        .end(new DateTime(2012, 04, 19, 0, 0, 0, 0))
        .tags(List("java"))
        .toEvent

    private def initData {
        EventDao.saveEvent( "test", eventDevoxx )
        EventDao.saveEvent( "test", eventJava )
    }
}
