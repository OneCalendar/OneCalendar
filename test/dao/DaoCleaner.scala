package dao

import configuration.injection.MongoConnectionPool
import configuration.injection.MongoProp._
import org.scalatest.{BeforeAndAfter, FunSuite}
import models.{EventTypeClass, ICalStreamTypeClass}
import com.mongodb.casbah.Imports._

trait DaoCleaner extends FunSuite with BeforeAndAfter {
    object StreamDaoCleaner extends MongoOperations with ICalStreamTypeClass with MongoConnectionPool {
        def drop()(implicit dbName: MongoDbName) = delete(MongoDBObject())
    }

    object EventDaoCleaner extends MongoOperations with EventTypeClass with MongoConnectionPool {
        def drop()(implicit dbName: MongoDbName) = delete(MongoDBObject())
    }

    implicit val dbName: MongoDbName = "test"

    before {
        StreamDaoCleaner.drop()
        EventDaoCleaner.drop()
    }
}