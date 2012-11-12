/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dao

import configuration.injection.MongoConfiguration
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import com.mongodb.{Mongo, DB}
import models.ICalStream

class ICalStreamDaoTest  extends FunSuite with ShouldMatchers with BeforeAndAfter {


    implicit val mongoConfigurationTesting = MongoConfiguration("test")

    val db: DB = {
        val mongo: Mongo = new Mongo()
        val db: DB = mongo.getDB(mongoConfigurationTesting.dbName)
        db
    }

    before {
        db.requestStart
        db.getCollection("icalstreams").drop
    }

    after {
        db.requestDone
    }

    test("find ical streams to load") {
        ICalStreamsDao.save(ICalStream("hello","tag"))
        ICalStreamsDao.findAll() should have size 1
    }

}
