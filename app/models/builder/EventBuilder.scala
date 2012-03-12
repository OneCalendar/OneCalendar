package models.builder

import org.joda.time.DateTime
import models.Event

/**
 * User: ugo
 * Date: 12/03/12
 */

class EventBuilder {

    private var title: String = _
    private var begin: DateTime = _
    private var end: DateTime = _
    private var location: String =_
    private var description: String = _
    private var tags: List[String] = _
    
    def toEvent: Event = Event( title, begin, end, location, description, tags )
    
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