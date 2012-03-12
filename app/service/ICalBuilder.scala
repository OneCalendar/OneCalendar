package service

import models.Event
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import property._
import net.fortuna.ical4j.data.CalendarOutputter
import java.io.StringWriter

class ICalBuilder {
    
    val ID: String = "-//OneCalendarToMeetThemAll//FR"

    def buildCalendar( events: List[Event] ): String = {
        val componentList: ComponentList = new ComponentList()
        events.map(
            event =>
                componentList.add( buildVEvent(event) )
        )
        val writer = new StringWriter()
        val calendar: Calendar = new Calendar(componentList)
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(new ProdId(ID));
        calendar.getProperties().add(CalScale.GREGORIAN);
        new CalendarOutputter().output(calendar, writer)
        writer.toString
    }

    private def buildVEvent( event: Event ): VEvent = {
        val vevent: VEvent = new VEvent

        vevent.getProperties.add( new Uid(ID + String.valueOf(event.hashCode())))

        vevent.getProperties.add( new DtStart( new DateTime( event.begin.toDate ) ) )
        vevent.getProperties.add( new DtEnd( new DateTime( event.end.toDate ) ) )

        vevent.getProperties.add( new Summary( event.title ) )
        vevent.getProperties.add( new Description( event.description ) )
        vevent.getProperties.add( new Location( event.location ) )

        vevent
    }
}