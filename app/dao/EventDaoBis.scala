package dao

import models.Event
import reactivemongo.api.DB
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.EventJsonFormatter._
import scala.concurrent.Future
import play.api.libs.json.Json

object EventDaoBis {

    def saveEvent(event: Event)(implicit db: DB) = db[JSONCollection]("events").save(event)

    // TODO from a given date
    def findByTags(tags: Set[String])(implicit db: DB): Future[Set[Event]] = {
        //val query = ("tags" $in tags.map(_.toUpperCase)) ++ ( "begin" $gt now() )
        val query = Json.obj("tags" -> Json.obj("$in" -> tags.map(_.toUpperCase)))
        db[JSONCollection]("events").find(query).cursor[Event].collect[Set]()
    }
}