package service

import org.scalatest.matchers._
import org.joda.time.DateTime
import org.scalatest._
import models.Event
import models.builder.EventBuilder

class ICalBuilderTest extends FunSuite
with ShouldMatchers
with BeforeAndAfter {

    var ical: String = _

    before {
        val events: List[Event] = List(
            new EventBuilder()
                .uid( "0" )
                .title( "Event1" )
                .begin( new DateTime() )
                .end( new DateTime() )
                .location( "place1" )
                .description( "super java conf" )
                .tags( List("java") )
                .toEvent,

            new EventBuilder()
                .uid( "1" )
                .title( "Event2" )
                .begin( new DateTime() )
                .end( new DateTime() )
                .location( "place2" )
                .description( "super scala conf" )
                .tags( List("scala") )
                .toEvent
        )

        ical = new ICalBuilder().buildCalendar(events)
    }

    test("ical should be a valid iCal") {
        ical should startWith ("BEGIN:VCALENDAR")
        ical should include ("VERSION:2.0")
        ical should include ("PRODID:")
    }

    test("ical should have all events") {
        ical should include ("SUMMARY:Event1")
        ical should include ("SUMMARY:Event2")
    }
    
    test("event should have all properties") {
        val event = new ICalBuilder().buildCalendar(
            List( new EventBuilder()
                .uid( "0" )
                .title( "Event1" )
                .begin( new DateTime( 2010, 01, 01, 12, 0, 0 ) )
                .end( new DateTime( 2010, 01, 01, 14, 0, 0 ) )
                .location( "place1" )
                .description( "super java conf" )
                .tags( List("java") )
                .toEvent
            )
        )

        event should include ( "DTSTART:20100101T120000" )
        event should include ( "DTEND:20100101T140000" )
        event should include ( "DESCRIPTION:super java conf" )
        event should include ( "LOCATION:place1" )
        event should include ( "UID:0" )
    }
}