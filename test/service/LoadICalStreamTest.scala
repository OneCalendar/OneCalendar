package service

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import dao.EventDao

class LoadICalStreamTest extends FunSuite with ShouldMatchers {

    //https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/-4b4d566cd18fd63d76c6cc6ea84086cf/basic.ics 
    test("should parse iCal stream"){
        val url : String = "https://www.google.com/calendar/ical/u74tb1k9n53bnc5qsg3694p2l4%40group.calendar.google.com/private-4b4d566cd18fd63d76c6cc6ea84086cf/basic.ics"
        val iCalService : LoadICalStream = new LoadICalStream()
        iCalService.parseLoad(url,"test")
        EventDao.getEventsCollection("test").count() should be (55)
    }
}
