package api.icalendar

import java.util.ArrayList
import fr.scala.util.collection.CollectionsUtils
import models.Event
import net.fortuna.ical4j.model.{DateTime, Calendar, ComponentList, Component}
import java.net.URI
import java.io.{StringWriter, Writer, InputStream}
import net.fortuna.ical4j.data.{CalendarOutputter, ParserException, CalendarBuilder}

case class ICalendarParsingError(message: String, e: Exception)

object ICalendar extends CollectionsUtils {
    type ICalendarSource = InputStream

    def retrieveVEvents(icalSource: ICalendarSource): Either[ICalendarParsingError, List[VEvent]] = {
        try{
            val components: List[ AnyRef ] = parseSource(icalSource)
            Right( components map toVEvent)
        } catch {
            case e: ParserException => Left(ICalendarParsingError("Parsing error from ICalendar", e))
        }
    }

    type ICalendarRender = String

    val ID: String = "-//OneCalendarToMeetThemAll//FR"

    def buildCalendar(events: List[Event]): ICalendarRender = {
        val componentList: ComponentList = new ComponentList()

        events.map(event => componentList.add(buildVEvent(event)))

        val calendar: Calendar = buildCalendar(componentList)

        serializeCalendar(calendar)
    }

    private def buildVEvent(event: Event): net.fortuna.ical4j.model.component.VEvent = {
        val vevent: net.fortuna.ical4j.model.component.VEvent = new net.fortuna.ical4j.model.component.VEvent

        vevent.getProperties.add(new net.fortuna.ical4j.model.property.Uid(event.uid))

        vevent.getProperties.add(new net.fortuna.ical4j.model.property.DtStart(new DateTime(event.begin.toDate)))
        vevent.getProperties.add(new net.fortuna.ical4j.model.property.DtEnd(new DateTime(event.end.toDate)))

        vevent.getProperties.add(new net.fortuna.ical4j.model.property.Summary(event.title))
        vevent.getProperties.add(new net.fortuna.ical4j.model.property.Description(event.description))
        vevent.getProperties.add(new net.fortuna.ical4j.model.property.Location(event.location))
        if (event.url != null) {
            vevent.getProperties.add(new net.fortuna.ical4j.model.property.Url(new URI(event.url)))
        }

        vevent
    }

    private def buildCalendar(componentList: ComponentList): Calendar = {
        val calendar: Calendar = new Calendar(componentList)
        calendar.getProperties.add(net.fortuna.ical4j.model.property.Version.VERSION_2_0)
        calendar.getProperties.add(new net.fortuna.ical4j.model.property.ProdId(ID))
        calendar.getProperties.add(net.fortuna.ical4j.model.property.CalScale.GREGORIAN)
        calendar.getProperties.add(new net.fortuna.ical4j.model.property.XProperty("X-WR-CALNAME", "OneCalendar"))
        calendar.getProperties.add(new net.fortuna.ical4j.model.property.XProperty("X-WR-CALDESC", "My Calendar to Meet them All"))
        calendar
    }

    private def serializeCalendar(calendar: Calendar): String = {
        val writer: Writer = new StringWriter()
        new CalendarOutputter().output(calendar, writer)
        writer.toString
    }

    private val parseSource: (InputStream) => ArrayList[AnyRef] = (src: InputStream) => new CalendarBuilder().build(src).getComponents(Component.VEVENT).asInstanceOf[ArrayList[AnyRef]]
    private val toVEvent: (AnyRef) => VEvent = (el: AnyRef) => el match { case vevent: net.fortuna.ical4j.model.component.VEvent => new VEvent(vevent) }
}