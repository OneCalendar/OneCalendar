package dao.configuration.injection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}

object MongoProp {
    type MongoDbName = String
    type MongoCollectionName = String
}

trait MongoConnectionPool {
    import MongoProp._

    implicit def retrieveMongoCollection(collectionName: MongoCollectionName)(implicit dbName: MongoDbName): MongoCollection = {
        val pool: MongoDB = MongoPool()
        pool(collectionName)
    }

    private object MongoPool {
        def apply()(implicit dbName: MongoDbName): MongoDB = connection(dbName)

        private val connection: MongoConnection = {
            val options: MongoOptions = new MongoOptions()
            options.setConnectionsPerHost(100)
            MongoConnection(new ServerAddress("127.0.0.1"),options)
        }
    }
}