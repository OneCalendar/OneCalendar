package dao

import com.mongodb.{Mongo, DB}
import org.jongo.{MongoCollection, Jongo}
import models.Event

/**
 * User: amira
 * Date: 09/03/12
 */

object DaoModule {

  var mongo: Mongo  = new Mongo()

  def getEventsCollection(dbName :String): MongoCollection = {
    val db : DB = getDatabase(dbName)
    new Jongo(db).getCollection("events")
   }

   def getDatabase(dbname: String): DB = {
    mongo.getDB(dbname)
  }

  def stopConnection(){
     mongo.close()
   }

  def saveEvent (dbName :String, event :Event) {
    getEventsCollection(dbName).save(event)
  }
}