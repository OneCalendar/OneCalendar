package models.mapping

import api.icalendar.VEvent
import models.Event

trait Event$VEventMapping {
    implicit def convertEventToVEvent(event: Event): VEvent =
        VEvent(event.uid, event.title, event.begin, event.end, event.location, event.url, event.description)

    implicit def convertEventsToVEvents(events: List[Event]): List[VEvent] = events map convertEventToVEvent
}