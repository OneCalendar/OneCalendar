package api.icalendar

import java.net.URI
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property._
import org.joda.time.DateTime

object VEvent {
    // Don't panic ! the null value are catched just below - permit to build easily VEvent with no all properties which are not mandatory
    def apply(uid: String = null,
              summary: String = null, 
              startDate: DateTime = null, 
              endDate: DateTime = null, 
              location: String = null, 
              url: String = null, 
              description: String = null): VEvent = {
        
        val fVevent = new net.fortuna.ical4j.model.component.VEvent()

        if(uid != null) fVevent.getProperties.add(new Uid(uid))
        if(startDate != null) fVevent.getProperties.add(new DtStart(new net.fortuna.ical4j.model.DateTime(startDate.getMillis)))
        if(endDate != null) fVevent.getProperties.add(new DtEnd(new net.fortuna.ical4j.model.DateTime(endDate.getMillis)))
        if(summary != null) fVevent.getProperties.add(new Summary(summary))
        if(description != null) fVevent.getProperties.add(new Description(description))
        if(location != null) fVevent.getProperties.add(new Location(location))
        if(url != null) fVevent.getProperties.add(new Url(new URI(url)))

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

    // TODO cette méthode ne doit être visible que dans le package api.calendar
    def toICal4J: net.fortuna.ical4j.model.component.VEvent = vevent

    override def equals(vevent: Any): Boolean = vevent match {
        case that: VEvent => if ( this.uid == None || that.uid == None ) false else this.uid == that.uid
        case _ => false
    }

    // TODO méfions nous des getDate de fortuna
    private val toDate = (p: DateProperty) => new DateTime(p.getDate)
    private val toValue = (p: Property) => p.getValue
}