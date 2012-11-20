package api.icalendar

import fr.scala.util.collection.CollectionsUtils
import java.io.{StringWriter, Writer, InputStream}
import java.util.ArrayList
import net.fortuna.ical4j.data.{CalendarOutputter, ParserException, CalendarBuilder}
import net.fortuna.ical4j.model.{Calendar, ComponentList, Component}

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

    def buildCalendar(vEvents: List[VEvent]): ICalendarRender = {
        val componentList: ComponentList = new ComponentList()

        vEvents foreach ( e => componentList.add(e.toICal4J) )
        
        val calendar: Calendar = buildCalendar(componentList)

        serializeCalendar(calendar)
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