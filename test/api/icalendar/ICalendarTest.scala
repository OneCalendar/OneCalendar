package api.icalendar

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.net.URI
import com.google.common.io.Resources.getResource
import net.fortuna.ical4j.model.property._

class ICalendarTest extends FunSuite with ShouldMatchers {
    test("should retireve empty list when feed is empty") {
        val vEvents = ICalendar.retrieveVEvents( getResource("api/icalendar/empty.ics").openStream() )
        vEvents should be (Right(Nil))
    }

    test("should retireve VEvent from icalendar source") {
        val vEvents = ICalendar.retrieveVEvents( getResource("api/icalendar/singleEvent.ics").openStream() )
        vEvents.right.get should have size 1
        vEvents.right.get should contain (new VEvent(getVEvent()))
    }

    test("should retrieve empty list when feed is invalid") {
        val vEvents = ICalendar.retrieveVEvents( getResource("api/icalendar/invalid.ics").openStream() )
        vEvents.left.get.message should be ("Parsing error from ICalendar")
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