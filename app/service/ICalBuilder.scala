/*
 * This file is part of OneCalendar.
 *
 * OneCalendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OneCalendar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
 */

package service

import models.Event
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import property._
import net.fortuna.ical4j.data.CalendarOutputter
import java.io.{Writer, StringWriter}
import java.net.URI

class ICalBuilder {
    val ID: String = "-//OneCalendarToMeetThemAll//FR"

    def buildCalendar(events: List[Event]): String = {
        val componentList: ComponentList = new ComponentList()

        events.map(event => componentList.add(buildVEvent(event)))

        val calendar: Calendar = buildCalendar(componentList)

        serializeCalendar(calendar)
    }

    private def buildVEvent(event: Event): VEvent = {
        val vevent: VEvent = new VEvent

        vevent.getProperties.add(new Uid(event.uid))

        vevent.getProperties.add(new DtStart(new DateTime(event.begin.toDate)))
        vevent.getProperties.add(new DtEnd(new DateTime(event.end.toDate)))

        vevent.getProperties.add(new Summary(event.title))
        vevent.getProperties.add(new Description(event.description))
        vevent.getProperties.add(new Location(event.location))
        if (event.url != null) {
            vevent.getProperties.add(new Url(new URI(event.url)))
        }

        vevent
    }

    private def buildCalendar(componentList: ComponentList): Calendar = {
        val calendar: Calendar = new Calendar(componentList)
        calendar.getProperties.add(Version.VERSION_2_0)
        calendar.getProperties.add(new ProdId(ID))
        calendar.getProperties.add(CalScale.GREGORIAN)
        calendar.getProperties.add(new XProperty("X-WR-CALNAME", "OneCalendar"))
        calendar.getProperties.add(new XProperty("X-WR-CALDESC", "My Calendar to Meet them All"))
        calendar
    }

    private def serializeCalendar(calendar: Calendar): String = {
        val writer: Writer = new StringWriter()
        new CalendarOutputter().output(calendar, writer)
        writer.toString
    }
}