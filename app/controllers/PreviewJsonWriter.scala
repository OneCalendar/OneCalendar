package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._

trait PreviewJsonWriter {
    implicit val previewEventWriter: Writes[PreviewEvent] = (
        (__ \ "date").write[String] and
            (__ \ "title").write[String] and
            (__ \ "location").write[String]
        )(unlift(PreviewEvent.unapply))

    implicit val previewEventWriterWithEventTag = (__ \ "event").write(previewEventWriter)

    implicit val previewWriter: Writes[Preview] = (
        (__ \ "size").write[Long] and
            (__ \ "eventList").write(Writes.seq(previewEventWriterWithEventTag))
        )(unlift(Preview.unapply))
}