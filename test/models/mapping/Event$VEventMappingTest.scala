package models.mapping

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import models.Event
import api.icalendar.VEvent
import org.joda.time.DateTime

class Event$VEventMappingTest extends FunSuite with ShouldMatchers with Event$VEventMapping {
    test("should map dummy Event to VEvent") {
        val vEvent: VEvent = convertEventToVEvent(Event(title= "title", begin = null, end = null))
        vEvent.summary should be (Option("title"))
    }

    test("should map 'complete' Event to VEvent") {
        val start = new DateTime(123000L)
        val end = new DateTime(456000L)

        val event = Event(title= "title", begin = start, end = end, uid = "123", location = "location", description = "description", url = Some("url"))

        val vEvent: VEvent = convertEventToVEvent( event )

        vEvent.uid         should be (Option("123")        )
        vEvent.summary     should be (Option("title")      )
        vEvent.description should be (Option("description"))
        vEvent.location    should be (Option("location")   )
        vEvent.url         should be (Option("url")        )
        vEvent.startDate   should be (Option(start)        )
        vEvent.endDate     should be (Option(end)          )
    }

    test("should map 'complete' Events to VEvents") {
        val start = new DateTime(123000L)
        val end = new DateTime(456000L)

        val event = List(Event(title= "title", begin = start, end = end, uid = "123", location = "location", description = "description", url = Some("url")))

        val vEvents: List[VEvent] = convertEventsToVEvents( event )

        vEvents.head.uid         should be (Option("123")        )
        vEvents.head.summary     should be (Option("title")      )
        vEvents.head.description should be (Option("description"))
        vEvents.head.location    should be (Option("location")   )
        vEvents.head.url         should be (Option("url")        )
        vEvents.head.startDate   should be (Option(start)        )
        vEvents.head.endDate     should be (Option(end)          )
    }
}