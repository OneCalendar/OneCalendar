package dao

import configuration.injection.MongoConfiguration
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import com.mongodb.{Mongo, DB}
import models.ICalStream

class ICalStreamDaoTest  extends FunSuite with ShouldMatchers with BeforeAndAfter {


    implicit val mongoConfigurationTesting = MongoConfiguration("test")

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB(mongoConfigurationTesting.dbName)
        db
    }

    before {
        db.requestStart
        db.getCollection("icalstreams").drop
    }

    after {
        db.requestDone
    }

    test("find ical streams to load") {
        ICalStreamsDao.save(ICalStream("hello","tag"))
        ICalStreamsDao.findAll() should have size 1
    }

}
