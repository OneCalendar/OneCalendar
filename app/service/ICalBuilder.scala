/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package service

import models.Event
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import property._
import net.fortuna.ical4j.data.CalendarOutputter
import java.io.{Writer, StringWriter}

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

        vevent
    }

    private def buildCalendar(componentList: ComponentList): Calendar = {
        val calendar: Calendar = new Calendar(componentList)
        calendar.getProperties.add(Version.VERSION_2_0);
        calendar.getProperties.add(new ProdId(ID));
        calendar.getProperties.add(CalScale.GREGORIAN);
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