package dao.framework

import com.mongodb.casbah.Imports._

object MongoConnectionProperties {
    type MongoDbName = String
    type MongoCollectionName = String
}

trait MongoConnectionPool {
    import MongoConnectionProperties._

    implicit def retrieveMongoCollection(collectionName: MongoCollectionName)
                                        (implicit dbName: MongoDbName, /*en fait connection tout court*/pool: MongoDB): MongoCollection =
        pool(collectionName)
}