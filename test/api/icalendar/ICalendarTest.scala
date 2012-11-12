package api.icalendar

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.google.common.io.Resources.getResource
import net.fortuna.ical4j.model.property._
import java.net.URI
import java.io.InputStream

class ICalendarTest extends FunSuite with ShouldMatchers {
    test("should retireve VEvent from icalendar source null") {
        ICalendar.retrieveVEvents(null) should be (Nil)
    }

    ignore("should retireve VEvent from icalendar source") {
        val vEvents = ICalendar.retrieveVEvents( getResource("classpath://singleEvent.ics").openStream() )
        vEvents should have size 1
        vEvents should contain (new VEvent(getVEvent()))
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

object ICalendar {
    def retrieveVEvents(icalSource: InputStream): List[VEvent] = Nil
}