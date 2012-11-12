package api.icalendar

import org.joda.time.DateTime
import net.fortuna.ical4j.model.property.DateProperty
import net.fortuna.ical4j.model.Property

class VEvent(vevent: net.fortuna.ical4j.model.component.VEvent) {
    require(vevent != null, "requirement failed : net.fortuna.ical4j.model.component.VEvent should not be null")

    def uid: Option[ String ] = option(vevent.getUid, toValue)

    def summary: Option[ String ] = option(vevent.getSummary, toValue)

    def startDate: Option[ DateTime ] = option(vevent.getStartDate, toDate)

    def endDate: Option[ DateTime ] = option(vevent.getEndDate, toDate)

    def location: Option[ String ] = option(vevent.getLocation, toValue)

    def url: Option[ String ] = option(vevent.getUrl, toValue)

    def description: Option[ String ] = option(vevent.getDescription, toValue)

    override def equals(vevent: Any): Boolean = vevent match {
        case that: VEvent => if ( this.uid == None || that.uid == None ) false else this.uid == that.uid
        case _ => false
    }

    // TODO méfions nous des getDate de fortuna
    val toDate = (p: DateProperty) => new DateTime(p.getDate)
    val toValue = (p: Property) => p.getValue

    private def option[T, U <: Property](property: U, f: (U) => T): Option[T] =
        if ( property != null ) Option(f(property)) else None
}