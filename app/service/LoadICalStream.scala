/*
 * This file is part of OneCalendar.
 *
 * OneCalendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OneCalendar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
 */

package service

import java.net.URL
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.{Component, ComponentList}
import models.Event
import org.joda.time.DateTime
import dao.EventDao
import dao.configuration.injection.MongoConfiguration
import java.util.StringTokenizer
import models.builder.EventBuilder
import play.api.Logger

class LoadICalStream {
    
    val TAG_PATTERN : String = "#([\\w\\d\\p{L}]+)"
    val DB_NAME : String = "OneCalendar"


    def parseLoad(url: String, eventName: String = "" )( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteByOriginalStream(url)
        val urlCal = new URL(url)
        val builder = new CalendarBuilder()
        val cal = builder.build(urlCal.openStream())
        val components: ComponentList = cal.getComponents(Component.VEVENT)

        Logger.trace("to load %d events from %s".format (components.size,url))

        var nb = 0

        components.toArray.toList.map(_.asInstanceOf[Component]).foreach(arg => {
            import net.fortuna.ical4j.model.component._

            val vEvent: VEvent = arg.asInstanceOf[VEvent]

            val oneEvent: Event = new EventBuilder()
                .uid( vEvent.getUid.getValue )
                .title( vEvent.getSummary.getValue )
                .begin( new DateTime( vEvent.getStartDate.getDate ) )
                .end( new DateTime(vEvent.getEndDate.getDate) )
                .location( vEvent.getLocation.getValue )
                .originalStream(url)
                .description( vEvent.getDescription.getValue )
                .tags( getTagsFromDescription(vEvent.getDescription.getValue + (if(!eventName.isEmpty) " #" + eventName; else ""  ) ) )
                .toEvent

            if (oneEvent.end.isAfter(dbConfig.now)) {
                nb=nb+1
                EventDao.saveEvent( oneEvent )
            } else {
                Logger.warn("event %s not loaded because now is %s and it's already ended %s".format(oneEvent.title,new DateTime(dbConfig.now),oneEvent.end) )
            }
        })


        Logger.trace("been loaded %d events".format (nb))
    }

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
}
