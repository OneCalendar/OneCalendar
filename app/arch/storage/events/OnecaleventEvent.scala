package arch.storage.events

import models.Event

trait OnecaleventEvent
case class EventAdded(uid: String = "",
                      title: String = "",
                      begin: Long,
                      end: Long,
                      location: String = "",
                      description: String = "",
                      tags: List[String] = Nil,
                      originalStream: Option[String] = None,
                      url:Option[String] = None,
                      timestamp: Long
                     ) extends OnecaleventEvent {
    def this(event: Event, timestamp: Long) =
        this(event.uid,
             event.title,
             event.begin.getMillis,
             event.end.getMillis,
             event.location,
             event.description,
             event.tags,
             event.originalStream,
             event.url,
             timestamp
        )
}