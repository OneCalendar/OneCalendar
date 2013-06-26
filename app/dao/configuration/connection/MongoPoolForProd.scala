package dao.configuration.connection

import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}
import dao.framework.MongoConnectionProperties
import MongoConnectionProperties._

object MongoPoolForProd {

    private var props: MongoPoolProps = null

    private val connection: MongoConnection = {
        val options: MongoOptions = new MongoOptions()
        options.setConnectionsPerHost(100)
        MongoConnection(new ServerAddress(getHost, getPort), options)
    }

    def apply()(implicit dbName: MongoDbName): MongoDB = connection(dbName)

    def apply(properties: MongoPoolProps)(implicit dbName: MongoDbName): MongoDB = {
        if(props == null) props = properties
        connection(dbName)
    }

    def getHost: String = if(props != null) props.host else "127.0.0.1"
    def getPort: Int =  if(props != null) props.port else 27017
}

case class MongoPoolProps(host: String = "127.0.0.1", port: Int = 27017)
