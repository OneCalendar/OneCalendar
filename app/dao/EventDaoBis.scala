package dao

import models.Event
import reactivemongo.api.DB
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.EventJsonFormatter._
import scala.concurrent.Future
import play.api.libs.json.Json
import org.joda.time.DateTime
import reactivemongo.core.commands.{RawCommand, Count}
import reactivemongo.bson.BSONDocument

object EventDaoBis {

    def saveEvent(event: Event)(implicit db: DB) = db[JSONCollection]("events").save(event)

    def findByTags(tags: Set[String], sinceDate: DateTime = DateTime.now())(implicit db: DB): Future[Set[Event]] = {
        val query = Json.obj("tags" -> Json.obj("$in" -> tags.map(_.toUpperCase))) ++
                    Json.obj("begin" -> Json.obj("$gt" -> sinceDate.getMillis))
        db[JSONCollection]("events").find(query).cursor[Event].collect[Set]()
    }

    def findAllFromNow()(implicit db: DB): Future[Set[Event]] = {
        db[JSONCollection]("events")
            .find(Json.obj("begin" -> Json.obj("$gt" -> DateTime.now().getMillis)))
            .cursor[Event]
            .collect[Set]()
    }

    def listTags(sinceDate: DateTime = DateTime.now())(implicit db: DB): Future[Set[String]] = {
        val command = RawCommand(BSONDocument("distinct" -> "events",
                                              "key" -> "tags",
                                              "query" -> BSONDocument(
                                                  "begin" -> BSONDocument("$gt" -> sinceDate.getMillis))))

        db.command(command).map { bson => bson.getAs[Set[String]]("values").getOrElse(Set()) }
    }

    def countFutureEvents(sinceDate: DateTime = DateTime.now())(implicit db: DB): Future[Int] = {
        val bsonQuery = BSONDocument("begin" -> BSONDocument("$gt" -> sinceDate.getMillis))
        db.command((Count("events", Option(bsonQuery))))
    }

    def deleteByOriginalStream(originalStream: String)(implicit db: DB) = {
        db[JSONCollection]("events").remove(Json.obj("originalStream" -> originalStream))
    }
}