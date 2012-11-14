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

        val event = Event(title= "title", begin = start, end = end, uid = "123", location = "location", description = "description", url = "url")

        val vEvent: VEvent = convertEventToVEvent( event )

        vEvent.uid         should be (Option("123")        )
        vEvent.summary     should be (Option("title")      )
        vEvent.description should be (Option("description"))
        vEvent.location    should be (Option("location")   )
        vEvent.url         should be (Option("url")        )
        vEvent.startDate   should be (Option(start)        )
        vEvent.endDate     should be (Option(end)          )
    }
}