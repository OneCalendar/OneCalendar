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

import framework.MongoConnectionProperties
import MongoConnectionProperties._
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import ICalStreamDao._
import com.github.simplyscala.MongoEmbedDatabase
import com.mongodb.casbah.{MongoConnection, MongoDB}
import com.mongodb.{ServerAddress, MongoOptions}
import com.github.simplyscala.MongodProps
import models.ICalStream

class ICalStreamDaoTest  extends FunSuite with ShouldMatchers with MongoEmbedDatabase with BeforeAndAfterAll {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27017) }
    override def afterAll() { mongoStop(mongoProps) }

    implicit val dbName: MongoDbName = "test"
    implicit val pool: MongoDB = {
        val connection: MongoConnection = {
            val options: MongoOptions = new MongoOptions()
            options.setConnectionsPerHost(2)
            MongoConnection(new ServerAddress("127.0.0.1", 27017), options)
        }

        connection(dbName)
    }

    test("find ical streams to load") {
        findAll() should have size 0
        saveICalStream(ICalStream("hello",List("tag")))
        findAll() should have size 1
    }
}