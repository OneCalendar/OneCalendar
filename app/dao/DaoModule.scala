package dao

import com.mongodb.{Mongo, DB}
import org.jongo.Jongo

/**
 * User: amira
 * Date: 09/03/12
 */

object DaoModule {

    def getDatabase(dbname: String): DB = {
        startDB(dbname).getDatabase
    }

    def startDB(dbname: String): Jongo = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB(dbname)
        val jongo: Jongo = new Jongo(db)
        jongo;
    }
}