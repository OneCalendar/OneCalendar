package service

import org.scalatest.matchers.ShouldMatchers
import dao.EventDao
import com.mongodb.{Mongo, DB}
import org.scalatest.{BeforeAndAfter, FunSuite}
import dao.configuration.injection.MongoConfiguration

class LoadICalStreamTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

    implicit val mongoConfigurationTesting = MongoConfiguration( "test" )

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB( "test" )
        db
    }

    before {
        db.requestStart
    }

    after {
        db.requestDone
    }

    test("should parse iCal stream") {
        val url : String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/public/basic.ics"
        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad( url, "DEVOXX" )
        var count: Int = EventDao.findAll().size

        count should be > 50
        count should be < 100
    }
    
    test("should remove tags from event description"){
        val iCalService : LoadICalStream = new LoadICalStream()

        iCalService.getDescriptionWithoutTags("bla bla bla bla bla #toto") should be ("bla bla bla bla bla")
        iCalService.getDescriptionWithoutTags("bla bla bla bla bla #toto #titi #tata") should be ("bla bla bla bla bla")
        iCalService.getDescriptionWithoutTags("bla bla bla bla bla") should be ("bla bla bla bla bla")
    }
    
    test("should get tags from description"){
        val iCalService : LoadICalStream = new LoadICalStream()
        
        val tags : List[String] = iCalService.getTagsFromDescription("bla bla bla #Toto #tiTI")

        tags.size should  be (2)
        tags(0) should be ("TOTO")
        tags(1) should be ("TITI")
    }

}
