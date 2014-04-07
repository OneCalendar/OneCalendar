package service

import org.scalatest.FunSuite
import org.scalatest.matchers.ClassicMatchers
import play.api.libs.ws.{Response, WS}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class LoadDevoxxTest extends FunSuite with ClassicMatchers {
    test("devoxx 2014") {
        val scheduleRequest: Future[Response] = WS.url("http://cfp.devoxx.fr/api/conferences/devoxxFR2014/schedules/").get()
        val schedule = scheduleRequest.map { resp => resp.json }
                                      .map { jsvalue => jsvalue.validate[Schedule].get }

        /*for {
            s <- schedule
            link <- s.links
            expandedLink <- WS.url(link.href).get().map { _.json }
        }*/

        val futureSlotsSet = schedule.flatMap { s =>
            Future.sequence( s.links.map( link => WS.url(link.href).get().map { _.json }) )
                  .map { links =>
                      links.map { link => link.validate[Slots].get/*.asEither*/ }
                  }
        }

        val slots = futureSlotsSet.map { slotsSet => slotsSet.flatMap { slots => slots.slots } }

        //println(Await.result(futureSlotsSet, 1 second))
        println(Await.result(slots, 1 second))
    }

    case class Schedule(links: Set[Link])
    case class Link(href: String)

    case class Slots(slots: Set[Slot])
    case class Slot(roomId: String, roomName: String, roomCapacity: Int, fromTimeMillis: Long, toTimeMillis: Long, talk: Option[Talk])
    case class Talk(talkType: String, track: String, summaryAsHtml: String, id: String, speakers: Set[Speaker])
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

    /**
        {
            "roomId":"hall",
            "notAllocated":false,
            "fromTimeMillis":1397628000000,
            "break":{
                "id":"dej",
                "nameEN":"Welcome and Breakfast",
                "nameFR":"Accueil et petit-déjeuner",
                "room":{
                    "id":"hall",
                    "name":"Espace d'exposition",
                    "capacity":1500,
                    "setup":"special"
                }
            },
            "roomSetup":"special",
            "talk":null,
            "fromTime":"08:00",
            "toTimeMillis":1397633400000,
            "toTime":"09:30",
            "roomCapacity":1500,
            "roomName":"Espace d'exposition",
            "slotId":"dej_mercredi_16_8h0_9h30",
            "day":"mercredi"
        }

        {
           "roomId": "other_room",
           "notAllocated": false,
           "fromTimeMillis": 1397633400000,
           "break": null,
           "roomSetup": "sans objet",
           "talk":
           {
               "talkType": "Autres formats de conférence",
               "track": "Cloud, Big Data, NoSQL",
               "summaryAsHtml": "

Des passionnés de la donnée se retrouvent une journée entière pour améliorer les données de la plate-forme data.gouv.fr, c’est à dire les mettre en formats ouverts, fusionner des fichiers, détecter des erreurs, produire des visualisations et de nouvelles interprétations.

La seule limite sera votre imagination, vous êtes les acteurs de cette journée : producteurs de données issus de l'administration, datascientists, chercheurs, codeurs, designers, associations, experts du réseau Etalab, et toutes sortes de bonnes volontés travailleront ensemble dans des ateliers qu’ils auront choisis.
",
               "id": "ZYE-706",
               "speakers":
               [
                   {
                       "link":
                       {
                           "href": "http://cfp.devoxx.fr/api/conferences/devoxxFR2014/speakers/0756dcacee31c3025d51b0645b0eb777915bc76b",
                           "rel": "http://cfp.devoxx.fr/api/profile/speaker",
                           "title": "Pierre Pezziardi"
                       },
                       "name": "Pierre Pezziardi"
                   },
                   {
                       "link":
                       {
                           "href": "http://cfp.devoxx.fr/api/conferences/devoxxFR2014/speakers/1e7e179446a21fe6719d7ade5b0ed8d1b6c8051b",
                           "rel": "http://cfp.devoxx.fr/api/profile/speaker",
                           "title": "Simon Chignard"
                       },
                       "name": "Simon Chignard"
                   }
               ],
               "title": "OpenDataCamp",
               "lang": "fr",
               "summary": "Des passionnés de la donnée se retrouvent une journée entière pour améliorer les données de la plate-forme data.gouv.fr, c’est à dire les mettre en formats ouverts, fusionner des fichiers, détecter des erreurs, produire des visualisations et de nouvelles interprétations. La seule limite sera votre imagination, vous êtes les acteurs de cette journée : producteurs de données issus de l'administration, datascientists, chercheurs, codeurs, designers, associations, experts du réseau Etalab, et toutes sortes de bonnes volontés travailleront ensemble dans des ateliers qu’ils auront choisis."
           },
           "fromTime": "09:30",
           "toTimeMillis": 1397664000000,
           "toTime": "18:00",
           "roomCapacity": 100,
           "roomName": "Other room",
           "slotId": "other_other_room_mercredi_16_9h30_18h0",
           "day": "mercredi"
       },
     */
}