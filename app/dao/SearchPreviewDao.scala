package dao

import reactivemongo.api.DB
import scala.concurrent.Future
import models.{SearchPreview, Event}
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import models.EventJsonFormatter._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.Count
import org.joda.time.DateTime


object SearchPreviewDao {
    private val PREVIEW_SIZE = 4

    def findPreviewByTag(tags: Set[String])(implicit db: DB, now: DateTime): Future[SearchPreview] = {
        val bsonQuery = BSONDocument("tags" -> BSONDocument("$in" -> tags.map(_.toUpperCase))) ++ BSONDocument("begin" -> BSONDocument("$gt" -> now.getMillis))
        val jsonQuery = Json.obj("tags" -> Json.obj("$in" -> tags.map(_.toUpperCase))) ++ Json.obj("begin" -> Json.obj("$gt" -> now.getMillis))

        for {
            events <- db[JSONCollection]("events").find(jsonQuery)
                            .sort(Json.obj("begin" -> 1))
                            .cursor[Event]
                            .collect[Seq](PREVIEW_SIZE)
            counter <- db.command(Count("events", Option(bsonQuery)))
        } yield SearchPreview(counter, events)
    }
}