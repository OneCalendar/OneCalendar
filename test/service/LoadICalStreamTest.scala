package service

import org.scalatest.matchers.ShouldMatchers
import dao.EventDao
import com.mongodb.{Mongo, DB}
import org.scalatest.{BeforeAndAfter, FunSuite}
import dao.configuration.injection.MongoConfiguration._
import dao.configuration.injection.MongoConfiguration

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    //https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/-4b4d566cd18fd63d76c6cc6ea84086cf/basic.ics 
    implicit val mongoConfigurationTesting = MongoConfiguration( "test" )

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

    test("should parse iCal stream") {
        val url : String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/private-4b4d566cd18fd63d76c6cc6ea84086cf/basic.ics"
        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url )
        EventDao.getEventsCollection( "test" ).count.toInt should be > 50
    }
}
