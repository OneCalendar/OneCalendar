package dao

import com.mongodb.{DB, Mongo}
import org.jongo.MongoCollection
import models.Event
import org.joda.time.DateTime
import collection.immutable.List


/**
 * User: amira
 * Date: 09/03/12
 */

class DaoModuleTest extends AbstractDaoTest {

  test("connecting to mongodb test") {
     try{
        val db: DB = DaoModule.getDatabase("test")
        db.getName should be ("test")
     }   finally {
        //DaoModule.mongo.close()
     }
  }

  test("getting events collection") {
     val eventsCollection: MongoCollection = DaoModule.getEventsCollection("test")
     eventsCollection.getName should be ("events")
    }

  test("saving a new event") {
    val event : Event = new Event("1","BOF",new DateTime(2012,04,19,0,0,0,0),new DateTime(2012,04,19,0,0,0,0),"","", List("java", "devoxx"))
    DaoModule.saveEvent("test",event)

    val savedEvent  = DaoModule.getEventsCollection("test").count("{}")
    savedEvent should be (1)
  }

}
