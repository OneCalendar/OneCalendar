package dao

import com.mongodb.{DB, Mongo}


/**
 * User: amira
 * Date: 09/03/12
 */

class DaoModuleTest extends AbstractDaoTest {

    test("hello mongo") {
        var mongo : Mongo = new Mongo()
        try{
            val db: DB = DaoModule.getDatabase("test")
            db.getName should be ("test")
        }   finally {
            mongo.close()
        }
    }

}
