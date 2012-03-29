package dao.configuration.injection

import java.util.Date

case class MongoConfiguration(dbName: String, var now: Long = new Date().getTime())