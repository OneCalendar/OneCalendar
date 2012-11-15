package dao

import org.scalatest.FunSuite
import com.mongodb.casbah.Imports._
import com.mongodb.{ServerAddress, MongoOptions}
import dao.MongoPoolTest._
import models.EventTypeClass


/**
 * Exemple de l'utilisation de type class en définissant le pool de connection au niveau du dao grace au trait MongoConnectionPool
 */


trait MongoConnectionPoolTest {
    /*
    MARCHE PO PARCE qu'on peut pas avoir d'implicit parameter sur une fonction
    implicit val retrieveMongoCollection: (MongoDbName, String) => MongoCollection = {
        (dbName: MongoDbName, collectionName: String) => {
            val pool: MongoDB = MongoPool()(dbName)
            pool(collectionName)
        }
    }*/
    
    implicit def retrieveMongoCollection(collectionName: String)(implicit dbName: MongoDbName): MongoCollection = {
        val collFun = MongoPoolTest()
        collFun(collectionName)
    }
}

object MongoPoolTest {
    type MongoDbName = String

    def apply()(implicit dbName: MongoDbName) = connection(dbName)

    private val connection: MongoConnection = {
        val options: MongoOptions = new MongoOptions()
        options.setConnectionsPerHost(100)
        MongoConnection(new ServerAddress("127.0.0.1"),options)
    }
}

object MyEventDao extends MongoOperationsTest with MongoConnectionPoolTest with EventTypeClass {

    def toto()(implicit dbName: MongoDbName) {
        print()
    }
}

class TestDao extends FunSuite {

    test("test") {
        implicit val dbName: MongoPoolTest.MongoDbName = "test"
        MyEventDao.toto()
    }
}

trait MongoOperationsTest {
    def print[T]()(implicit tc: MongoDbModel[T], retrieveMongoCollection: (String) => MongoCollection): Unit = {

        // retrieveMongoCollection(dbName, tc.collectionName) retourne une collection mongodb
        // qui nous permet d'effectuer toutes les opérations mongo de base
        println("TEST DAO : " + retrieveMongoCollection(tc.collectionName).getFullName())
    }
}