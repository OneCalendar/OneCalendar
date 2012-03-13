package dao

import models.Event
import org.joda.time.DateTime
import collection.immutable.List
import com.mongodb._
import org.scalatest.{BeforeAndAfter}


/**
 * User: amira
 * Date: 09/03/12
 */

class DaoModuleTest extends AbstractDaoTest with BeforeAndAfter {

    test("connecting to mongodb test") {
        try {
            val db: DB = DaoModule.getDatabase("test")
            db.getName should be("test")
        } finally {
        }
    }

    test("getting events collection") {
        val eventsCollection: DBCollection = DaoModule.getEventsCollection("test")
        eventsCollection.getName should be("events")
    }



    test("saving a new event") {
        val event: Event = new Event("1", "BOF", new DateTime(2012, 04, 19, 0, 0, 0, 0), new DateTime(2012, 04, 19, 0, 0, 0, 0), "", "", List("java", "devoxx"))
        DaoModule.saveEvent("test", event)

        val eventsCollection: DBCollection = DaoModule.getEventsCollection("test")
        val savedEvent = eventsCollection.count()
        savedEvent should be(1)

        val one: DBObject = eventsCollection.findOne()

        DaoModule.fromDbObject2Event(one) should be(event)

    }

    def after() {
        DaoModule.getDatabase("test").requestDone()
    }
}
