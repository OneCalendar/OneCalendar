/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private var originalStream: String = _

    def toEvent: Event = Event( uid, title, begin, end, location, description, tags, originalStream )

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

    def originalStream( originalStream: String) : EventBuilder = {
        this.originalStream = originalStream
        this
    }
}