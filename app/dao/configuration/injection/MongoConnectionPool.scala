package dao.configuration.injection

import com.mongodb.casbah.Imports._

object MongoPoolProperties {
    type MongoDbName = String
    type MongoCollectionName = String

    private var props: MongoPoolProps = null

    def apply(properties: MongoPoolProps) = if(props == null) props = properties

    def getHost: String = if(props != null) props.host else "127.0.0.1"
    def getPort: Int =  if(props != null) props.port else 27017
}

trait MongoConnectionPool {
    import MongoPoolProperties._

    implicit def retrieveMongoCollection(collectionName: MongoCollectionName)
                                        (implicit dbName: MongoDbName, /*en fait connection tout court*/pool: MongoDB): MongoCollection =
        pool(collectionName)
}

case class MongoPoolProps(host: String = "127.0.0.1", port: Int = 27017)