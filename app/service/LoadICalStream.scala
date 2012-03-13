package service

import java.net.URL
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.{Component, ComponentList}
import models.Event
import org.joda.time.DateTime
import dao.EventDao
import dao.configuration.injection.MongoConfiguration

class LoadICalStream {

    def parseLoad(url: String )
                 ( implicit dbConfig: MongoConfiguration = MongoConfiguration( "OneCalendar" ) ) {

        EventDao.deleteAll()
        val urlCal = new URL(url)
        val builder = new CalendarBuilder();
        val cal = builder.build(urlCal.openStream());
        val components: ComponentList = cal.getComponents(Component.VEVENT)


        components.toArray.toList.map(_.asInstanceOf[Component]).foreach(arg => {
            import net.fortuna.ical4j.model.component._

            val event: VEvent = arg.asInstanceOf[VEvent]

            val oneEvent: Event = new Event(event.getUid.getValue,
                event.getSummary.getValue,
                new DateTime(event.getStartDate.getDate),
                new DateTime(event.getEndDate.getDate),
                event.getLocation.getValue,
                event.getDescription.getValue,
                List("devoxx", "java"))


            EventDao.saveEvent(oneEvent)
        })
    }


}
