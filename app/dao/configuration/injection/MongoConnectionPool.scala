package dao.configuration.injection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}

object MongoProp {
    type MongoDbName = String
}

trait MongoConnectionPool {
    import MongoProp.MongoDbName

    implicit def retrieveMongoCollection(collectionName: String)(implicit dbName: MongoDbName): MongoCollection = {
        val collFun = MongoPool()
        collFun(collectionName)
    }

    private object MongoPool {
        def apply()(implicit dbName: MongoDbName) = connection(dbName)

        private val connection: MongoConnection = {
            val options: MongoOptions = new MongoOptions()
            options.setConnectionsPerHost(100)
            MongoConnection(new ServerAddress("127.0.0.1"),options)
        }
    }
}