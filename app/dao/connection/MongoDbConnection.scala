package dao.connection

import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.DB
import play.api.Play.current

trait MongoDbConnection {
	def db: DB
}

trait ProdMongoDbConnection extends MongoDbConnection {
	val db = ReactiveMongoPlugin.db
}