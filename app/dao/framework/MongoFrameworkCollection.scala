package dao.framework

import com.mongodb.casbah.Imports._

object MongoConnectionProperties {
    type MongoDbName = String
    type MongoCollectionName = String
}

trait MongoFrameworkCollection {
    import MongoConnectionProperties._

    implicit def retrieveMongoCollection(collectionName: MongoCollectionName)
                                        (implicit connection: MongoDB): MongoCollection =
        connection(collectionName)
}