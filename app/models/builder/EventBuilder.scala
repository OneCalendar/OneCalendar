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

package models.builder

import org.joda.time.DateTime
import models.Event

class EventBuilder {

    private var uid: String = _
    private var title: String = _
    private var begin: DateTime = _
    private var end: DateTime = _
    private var location: String =_
    private var description: String = _
    private var tags: List[String] = _
    
    def toEvent: Event = Event( uid, title, begin, end, location, description, tags )

    def uid( uid: String ): EventBuilder = {
        this.uid = uid
        this
    }

    def title( title: String ): EventBuilder = {
        this.title = title
        this
    }

    def begin( begin: DateTime ): EventBuilder = {
        this.begin = begin
        this
    }

    def end( end: DateTime ): EventBuilder = {
        this.end = end
        this
    }

    def location( location: String ): EventBuilder = {
        this.location = location
        this
    }

    def description( description: String ): EventBuilder = {
        this.description = description
        this
    }

    def tags( tags: List[String] ): EventBuilder = {
        this.tags = tags
        this
    }
}