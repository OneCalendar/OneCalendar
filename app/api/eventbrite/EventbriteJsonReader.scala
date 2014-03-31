package api.eventbrite

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads

trait EventbriteJsonReader {
    implicit val venueReader: Reads[Venue] = (
        (__ \ "address").readNullable[String] and
        (__ \ "address_2").readNullable[String] and
        (__ \ "city").readNullable[String] and
        (__ \ "region").readNullable[String] and
        (__ \ "country").readNullable[String] and
        (__ \ "postal_code").readNullable[String]
    )(Venue)

    implicit val eventbriteEventReader: Reads[EventbriteEvent] =
        (
            (__ \ "id").readNullable[Long] and
            (__ \ "title").readNullable[String] and
            (__ \ "start_date").readNullable[String] and
            (__ \ "description").readNullable[String] and
            (__ \ "end_date").readNullable[String] and
            (__ \ "tags").readNullable[String] and
            (__ \ "timezone_offset").readNullable[String] and
            (__ \ "url").readNullable[String] and
            (__ \ "venue").readNullable[Venue]
            tupled
        ).map( event => EventbriteEvent(event._1.map(_.toString), event._2,event._3,event._4,event._5,event._6,event._7,event._8,event._9) )

    // lire tous les event dans tes events     TODO si a la fin de la classe test play lance expcetion !!!!!!
    private val eventsReader = Reads[List[JsValue]]{ js => JsSuccess((__ \ "events" \\ "event")(js)) }

    // Ensuite tu vas transformer en Reads[List[EventbriteEvent]] en mixant un Reads.map et un List.foldLeft
    implicit val eventbriteResponseReader: Reads[JsResult[scala.Seq[EventbriteEvent]]] = eventsReader.map{ (l: List[JsValue]) =>
        l.foldLeft( JsSuccess(Seq()): JsResult[Seq[EventbriteEvent]] ){ (res, jsvalue) =>
            val jsr: JsResult[EventbriteEvent] = Json.fromJson[EventbriteEvent](jsvalue)
            for{
                seq <- res
                evt <- jsr
            } yield( seq :+ evt )
        }
    }
}
