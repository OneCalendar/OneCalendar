package models.mapping

import models.Event
import api.icalendar.VEvent

trait Event$VEventMapping {
    implicit def convertEventToVEvent(event: Event): VEvent =
        VEvent(event.uid, event.title, event.begin, event.end, event.location, event.url, event.description)
}