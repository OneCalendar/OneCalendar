package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import models.{Event, SearchPreview}

trait PreviewJsonWriter {

    implicit val eventWriter = new Writes[Event] {
        def writes(e: Event): JsValue = {
            println("event")
            Json.obj(
                "uid" -> e.uid,
                "title" -> e.title,
                "begin" -> e.begin,
                "end" -> e.end,
                "location" -> e.location,
                "description" -> e.description,
                "tags" -> e.tags,
                "originalStream" -> e.originalStream,
                "url" -> e.url
            )
        }
    }

    implicit val previewEventWriterWithEventTag = (__ \ "event").write(eventWriter)

    implicit val previewWriter: Writes[SearchPreview] = (
        (__ \ "size").write[Long] and
            (__ \ "eventList").write(Writes.seq(previewEventWriterWithEventTag))
        )(unlift(SearchPreview.unapply))
}
