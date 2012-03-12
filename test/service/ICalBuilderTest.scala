package service

import org.scalatest.matchers._
import org.joda.time.DateTime
import org.scalatest._
import models.Event
import scala.util.matching.Regex

class ICalBuilderTest extends FunSuite
with ShouldMatchers
with BeforeAndAfter {

    var ical: String = _

    before {
        val events: List[Event] = List(
            Event("Event1", new DateTime(), new DateTime(), "place1", "super java conf", List("java") ),
            Event("Event2", new DateTime(), new DateTime(), "place2", "super scala conf", List("scala") )
        )

        ical = new ICalBuilder().buildCalendar(events)
    }

    test("ical should be a valid iCal") {
        //println("ical: " + ical)
        ical should include ("BEGIN:VCALENDAR")
    }

    test("ical should have all events") {
        ical should include ("SUMMARY:Event1")
        ical should include ("SUMMARY:Event2")
    }
    
    test("event should have all properties") {
        val event = new ICalBuilder().buildCalendar(
            List( Event(
                "Event1",
                new DateTime( 2010, 01, 01, 12, 0, 0 ),
                new DateTime( 2010, 01, 01, 14, 0, 0 ),
                "place1",
                "super java conf",
                List( "java" )
            ))
        )

        event should include ( "DTSTART:20100101T120000" )
        event should include ( "DTEND:20100101T140000" )
        event should include ( "DESCRIPTION:super java conf" )
        event should include ( "LOCATION:place1" )
    }


}