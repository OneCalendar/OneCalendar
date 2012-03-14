package service

import java.net.URL
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.{Component, ComponentList}
import models.Event
import org.joda.time.DateTime
import dao.EventDao
import dao.configuration.injection.MongoConfiguration
import java.util.StringTokenizer

//import util.matching.Regex

class LoadICalStream {
    
    val TAG_PATTERN : String = "#[a-zA-Z1-9]+"
    val DB_NAME : String = "OneCalendar"

    def getDescriptionWithoutTags(s: String):String = {
        val description : String = s.replaceAll(TAG_PATTERN,"")
        description.trim()
    }

    def getTagsFromDescription(s: String): scala.List[String] = {
        var tags : List[String]= List()
        val tokenizer: StringTokenizer = new StringTokenizer(s)
        while (tokenizer.hasMoreTokens()) {
            var token : String = tokenizer.nextToken()
            if(token.matches(TAG_PATTERN)){
                tags=tags:+(token.replace("#","").trim().toUpperCase())
            }

        }
        tags
    }

    def parseLoad(url: String, eventName: String ="" )
                 ( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

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
                getDescriptionWithoutTags(event.getDescription.getValue),
                getTagsFromDescription(event.getDescription.getValue))


            EventDao.saveEvent(oneEvent)
        })
    }


}
