package api.icalendar

import org.joda.time.DateTime
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property._
import java.net.URI

object VEvent {
    def apply(uid: String, summary: String, startDate: DateTime, endDate: DateTime, location: String, url: String, description: String): VEvent = {
        val fVevent = new net.fortuna.ical4j.model.component.VEvent()

        fVevent.getProperties.add(new Uid(uid))
        fVevent.getProperties.add(new DtStart(new net.fortuna.ical4j.model.DateTime(startDate.getMillis)))
        fVevent.getProperties.add(new DtEnd(new net.fortuna.ical4j.model.DateTime(endDate.getMillis)))
        fVevent.getProperties.add(new Summary(summary))
        fVevent.getProperties.add(new Description(description))
        fVevent.getProperties.add(new Location(location))
        fVevent.getProperties.add(new Url(new URI(url)))

        new VEvent(fVevent)
    }
}

class VEvent(vevent: net.fortuna.ical4j.model.component.VEvent) {
    require(vevent != null, "requirement failed : net.fortuna.ical4j.model.component.VEvent should not be null")

    def uid: Option[ String ] = Option(vevent.getUid) map toValue

    def summary: Option[ String ] = Option(vevent.getSummary) map toValue

    def startDate: Option[ DateTime ] = Option(vevent.getStartDate) map toDate

    def endDate: Option[ DateTime ] = Option(vevent.getEndDate) map toDate

    def location: Option[ String ] = Option(vevent.getLocation) map toValue

    def url: Option[ String ] = Option(vevent.getUrl) map toValue

    def description: Option[ String ] = Option(vevent.getDescription) map toValue

    override def equals(vevent: Any): Boolean = vevent match {
        case that: VEvent => if ( this.uid == None || that.uid == None ) false else this.uid == that.uid
        case _ => false
    }

    // TODO méfions nous des getDate de fortuna
    private val toDate = (p: DateProperty) => new DateTime(p.getDate)
    private val toValue = (p: Property) => p.getValue
}