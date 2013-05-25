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

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import ICalStreamDao._
import models.ICalStream
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}

class ICalStreamDaoTest  extends FunSuite with ShouldMatchers with DaoCleaner with MongoEmbedDatabase with BeforeAndAfterAll {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27017) }
    override def afterAll() { mongoStop(mongoProps) }

    test("find ical streams to load") {
        findAll() should have size 0
        saveICalStream(ICalStream("hello",List("tag")))
        findAll() should have size 1
    }
}