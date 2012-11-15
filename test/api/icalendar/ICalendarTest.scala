package api.icalendar

import org.scalatest.matchers.ShouldMatchers
import java.net.URI
import com.google.common.io.Resources.getResource
import net.fortuna.ical4j.model.property._
import models.Event
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FunSuite}
import api.icalendar.ICalendar._
import models.mapping.Event$VEventMapping

class ICalendarTest extends FunSuite with ShouldMatchers with BeforeAndAfter with Event$VEventMapping {
    var ical: String = _

    before {
        val events: List[Event] = List(
            Event(
                uid = "0",
                title = "Event1",
                begin = new DateTime(),
                end = new DateTime(),
                location = "place1",
                description = "super java conf",
                tags = List("java")
            ),

            Event(
                uid = "1",
                title = "Event2",
                begin = new DateTime(),
                end = new DateTime(),
                location = "place2",
                description = "super scala conf",
                tags = List("scala")
            )
        )

        ical = buildCalendar(events)
    }

    test("should retireve empty list when feed is empty") {
        val vEvents = retrieveVEvents( getResource("api/icalendar/empty.ics").openStream() )
        vEvents should be (Right(Nil))
    }

    test("should retireve VEvent from icalendar source") {
        val vEvents = retrieveVEvents( getResource("api/icalendar/singleEvent.ics").openStream() )
        vEvents.right.get should have size 1
        vEvents.right.get should contain (new VEvent(getVEvent()))
    }

    test("should retrieve empty list when feed is invalid") {
        val vEvents = retrieveVEvents( getResource("api/icalendar/invalid.ics").openStream() )
        vEvents.left.get.message should be ("Parsing error from ICalendar")
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
        val event = buildCalendar(
            List( Event(
                uid = "0",
                title = "Event1",
                begin = new DateTime( 2010, 01, 01, 12, 0, 0 ),
                end = new DateTime( 2010, 01, 01, 14, 0, 0 ),
                location = "place1",
                description = "super java conf",
                tags = List("java")
            ))
        )

        event should include ( "DTSTART:20100101T120000" )
        event should include ( "DTEND:20100101T140000" )
        event should include ( "DESCRIPTION:super java conf" )
        event should include ( "LOCATION:place1" )
        event should include ( "UID:0" )
    }

    ignore("should retrieve 2 valid events when an event is invalid") {
        val vEvents = retrieveVEvents( getResource("api/icalendar/twoValidOneInvalidEvent.ics").openStream() )
        vEvents match {
            case Right(list) => list  should have size (2)
            case Left(err) => fail(err.e)
        }
        //cause : new URI("marketing%zenika.com@gtempaccount.com")
    }

    // TODO duplication voir VEventTest
    private def getVEvent(uid: Boolean = true): net.fortuna.ical4j.model.component.VEvent = {
        val vevent = new net.fortuna.ical4j.model.component.VEvent()

        if(uid) vevent.getProperties.add(new Uid("http://lacantine.org/events/reunion-d-etude-sur-le-projet-soho-de-la-ville-de-paris"))

        vevent.getProperties.add( new DtStart( new net.fortuna.ical4j.model.DateTime(123456L) ) )
        vevent.getProperties.add( new DtEnd( new net.fortuna.ical4j.model.DateTime(456457L) ) )

        vevent.getProperties.add(new Summary("title"))
        vevent.getProperties.add(new Description("description"))
        vevent.getProperties.add(new Location("location"))
        vevent.getProperties.add(new Url(new URI("url")))

        vevent
    }
}