package service

import models.Event
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import property._

class ICalBuilder {

    def buildCalendar( events: List[Event] ): String = {
        val componentList: ComponentList = new ComponentList()

        events.map(
            event =>
                componentList.add( buildVEvent(event) )
        )

        new Calendar( componentList ).toString
    }

    private def buildVEvent( event: Event ): VEvent = {
        val vevent: VEvent = new VEvent

        vevent.getProperties.add( new DtStart( new DateTime( event.begin.toDate ) ) )
        vevent.getProperties.add( new DtEnd( new DateTime( event.end.toDate ) ) )

        vevent.getProperties.add( new Summary( event.title ) )
        vevent.getProperties.add( new Description( event.description ) )
        vevent.getProperties.add( new Location( event.location ) )

        vevent
    }
}