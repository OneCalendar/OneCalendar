package dao.configuration.injection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}

object MongoProp {
    type MongoDbName = String
    type MongoCollectionName = String
    type MongoDbPort = Int
}

trait MongoConnectionPool {

    import MongoProp._

    implicit def retrieveMongoCollection(collectionName: MongoCollectionName)(implicit dbName: MongoDbName, port: MongoDbPort = 27017): MongoCollection = {
        val pool: MongoDB = MongoPool()
        pool(collectionName)
    }

    private object MongoPool {
        def apply()(implicit dbName: MongoDbName, port: MongoDbPort): MongoDB = connection(port)(dbName)

        private val connection: MongoDbPort => MongoConnection = {
            val options: MongoOptions = new MongoOptions()
            options.setConnectionsPerHost(100)
            (port: MongoDbPort) => MongoConnection(new ServerAddress("127.0.0.1", port), options)
        }
    }
}