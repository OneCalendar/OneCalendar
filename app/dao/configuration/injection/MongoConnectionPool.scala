package dao.configuration.injection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}

trait MongoConnectionPool {
    import MongoPool.MongoDbName

    implicit def retrieveMongoCollection(collectionName: String)(implicit dbName: MongoDbName): MongoCollection = {
        val collFun = MongoPool()
        collFun(collectionName)
    }
}

object MongoPool {
    type MongoDbName = String

    def apply()(implicit dbName: MongoDbName) = connection(dbName)

    private val connection: MongoConnection = {
        val options: MongoOptions = new MongoOptions()
        options.setConnectionsPerHost(100)
        MongoConnection(new ServerAddress("127.0.0.1"),options)
    }
}