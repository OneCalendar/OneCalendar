package api.icalendar

import org.joda.time.DateTime
import net.fortuna.ical4j.model.property.DateProperty
import net.fortuna.ical4j.model.Property

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