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
import reactivemongo.api.collections.default.BSONCollection
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

    /*def listTags()(implicit db: DB): Future[Set[String]] = {
        //        retrieveMongoCollection(EventMongoModel.collectionName).distinct("tags", query).toList.asInstanceOf[List[String]]

        //db.command(Count("events", Option(bsonQuery)))
        //db[BSONCollection]("events").find("tags").cursor[String].collect[Set]()
        val command = BSONDocument("aggregate" -> "orders",
            "$distinct" -> "tags"
        )

        db.command(RawCommand(command)).map { bson =>
            println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ : " + bson)
            Set()
        }
    }*/

    def countFutureEvents(sinceDate: DateTime = DateTime.now())(implicit db: DB): Future[Int] = {
        val bsonQuery = BSONDocument("begin" -> BSONDocument("$gt" -> sinceDate.getMillis))
        db.command((Count("events", Option(bsonQuery))))
    }

    def deleteByOriginalStream(originalStream: String)(implicit db: DB) = {
        db[JSONCollection]("events").remove(Json.obj("originalStream" -> originalStream))
    }
}