package api.icalendar

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import net.fortuna.ical4j.model.property._
import java.net.URI
import java.io.{FileInputStream, File}

class ICalendarTest extends FunSuite with ShouldMatchers {
    test("should retireve VEvent from icalendar source null") {
        ICalendar.retrieveVEvents(null) should be (Nil)
    }

    test("should retireve VEvent from icalendar source") {
        val vEvents = ICalendar.retrieveVEvents( new FileInputStream("test/api/icalendar/singleEvent.ics") )
        vEvents should have size 1
        vEvents should contain (new VEvent(getVEvent()))
    }

    test("should retireve empty list when fedd is invalid") {
        val vEvents = ICalendar.retrieveVEvents( new FileInputStream("test/api/icalendar/invalidFeed.ics") )
        vEvents should have size 0
    }

    test("should retireve empty list when feed is empty") {
        val vEvents = ICalendar.retrieveVEvents( new FileInputStream("test/api/icalendar/emptyFeed.ics") )
        vEvents should have size 0
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