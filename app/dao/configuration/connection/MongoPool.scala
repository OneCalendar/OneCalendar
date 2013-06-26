package dao.configuration.connection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}
import dao.configuration.injection.MongoPoolProperties._

trait MongoPool {
    def apply()(implicit dbName: MongoDbName): MongoDB
}

object MongoPoolForProd extends MongoPool {

    private val connection: MongoConnection = {
        val options: MongoOptions = new MongoOptions()
        options.setConnectionsPerHost(100)
        MongoConnection(new ServerAddress(getHost, getPort), options)
    }

    def apply()(implicit dbName: MongoDbName): MongoDB = connection(dbName)
}