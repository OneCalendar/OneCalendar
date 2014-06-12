package service

import com.mongodb.casbah.MongoDB
import dao.MongoDbEventDao._
import dao.framework.MongoConnectionProperties._
import models.Event
import org.joda.time.DateTime
import play.Logger
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object LoadDevoxx {


    case class Schedule(links: Set[Link])
    case class Link(href: String)

    case class Slots(slots: Set[Slot])
    case class Slot(roomId: String, roomName: String, roomCapacity: Int, fromTimeMillis: Long, toTimeMillis: Long, talk: Option[Talk])
    case class Talk(talkType: String, track: String, summaryAsHtml: String, id: String, speakers: Set[Speaker], title: String, summary: String)
    case class Speaker(name: String, link: SpeakerLinks)
    case class SpeakerLinks(rel: String)


    import play.api.libs.json.Json

    implicit val linkFormat = Json.format[Link]
    implicit val priceFormat = Json.format[Schedule]

    implicit val speakerLinksFormat = Json.format[SpeakerLinks]
    implicit val speakerFormat = Json.format[Speaker]
    implicit val talkFormat = Json.format[Talk]
    implicit val slotFormat = Json.format[Slot]
    implicit val slotsFormat = Json.format[Slots]

    def load(implicit now: () => Long, dbName: MongoDbName,  pool: MongoDB) = {
        val devoxxUrl: String = "http://cfp.devoxx.fr/api/conferences/devoxxFR2014/schedules/"


        val future: Future[Set[Slot]] = WS.url(devoxxUrl).get()
            .map { resp => resp.json}
            .map { jsvalue => jsvalue.validate[Schedule].get}
            .flatMap {
                s =>
                    Future.sequence(s.links.map(link => WS.url(link.href).get().map { _.json}))
                        .map { links => links.map { link => link.validate[Slots].get}}
            }.map {
                slotsSet => slotsSet.flatMap { slots => slots.slots}
            }

        val result: Set[Slot] = Await.result(future, 10 second)

        val events: Set[Event] = result.filter(_.talk.isDefined).map(slot2event(_, devoxxUrl))
        Logger.info("Chargement de %s events de devoxx".format(events.size))
        deleteByOriginalStream(devoxxUrl)
        events.foreach(saveEvent)
    }

    def slot2event(slot:Slot, url:String) = {
        val talk = slot.talk.get
        Event(uid = talk.id,title = talk.title,begin=new DateTime(slot.fromTimeMillis), end=new DateTime(slot.toTimeMillis),location = slot.roomName,description=talk.summary,tags=List("DEVOXX"),originalStream=Some(url))
    }
}
