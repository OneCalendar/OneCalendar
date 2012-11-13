package api.icalendar

import java.io.InputStream
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.data.{ParserException, CalendarBuilder}
import java.util.ArrayList
import fr.scala.util.collection.CollectionsUtils

case class ICalendarParsingError(message: String, e: Exception)

object ICalendar extends CollectionsUtils {
    def retrieveVEvents(icalSource: InputStream): Either[ICalendarParsingError, List[VEvent]] = {
        try{
            val components: List[ AnyRef ] = parseSource(icalSource)
            Right( components map toVEvent)
        } catch {
            case e: ParserException => Left(ICalendarParsingError("Parsing error from ICalendar", e))
        }
    }

    private val parseSource: (InputStream) => ArrayList[AnyRef] = (src: InputStream) => new CalendarBuilder().build(src).getComponents(Component.VEVENT).asInstanceOf[ArrayList[AnyRef]]
    private val toVEvent: (AnyRef) => VEvent = (el: AnyRef) => el match { case vevent: net.fortuna.ical4j.model.component.VEvent => new VEvent(vevent) }
}