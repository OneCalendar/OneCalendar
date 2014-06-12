package dao

import dao.connection.{ProdMongoDbConnection, MongoDbConnection}
import models.Event
import models.EventJsonFormatter._
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.{Count, RawCommand}

trait MongoDbEventDao extends EventDao with MongoDbConnection {

    def saveEvent(event: Event) = db[JSONCollection]("events").save(event)

    def findByTags(tags: Set[String], sinceDate: DateTime = DateTime.now()) = {
        val query = Json.obj("tags" -> Json.obj("$in" -> tags.map(_.toUpperCase))) ++
                    Json.obj("begin" -> Json.obj("$gt" -> sinceDate.getMillis))
        db[JSONCollection]("events").find(query).cursor[Event].collect[Set]()
    }

    def findAllFromNow() = {
        db[JSONCollection]("events")
            .find(Json.obj("begin" -> Json.obj("$gt" -> DateTime.now().getMillis)))
            .cursor[Event]
            .collect[Set]()
    }

    def listTags(sinceDate: DateTime = DateTime.now()) = {
        val command = RawCommand(BSONDocument("distinct" -> "events",
                                              "key" -> "tags",
                                              "query" -> BSONDocument(
                                                  "begin" -> BSONDocument("$gt" -> sinceDate.getMillis))))

        db.command(command).map { bson => bson.getAs[Set[String]]("values").getOrElse(Set()) }
    }

    def countFutureEvents(sinceDate: DateTime = DateTime.now()) = {
        val bsonQuery = BSONDocument("begin" -> BSONDocument("$gt" -> sinceDate.getMillis))
        db.command((Count("events", Option(bsonQuery))))
    }

    def deleteByOriginalStream(originalStream: String) = {
        db[JSONCollection]("events").remove(Json.obj("originalStream" -> originalStream))
    }
}

object MongoDbEventDao extends MongoDbEventDao with ProdMongoDbConnection