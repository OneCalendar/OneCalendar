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
import fr.scala.util.collection.CollectionsUtils
import play.api.Logger
import models.ICalStream
import com.mongodb._
import annotation.tailrec
import java.util

object ICalStreamsDao extends CollectionsUtils {

    private val log = Logger( "ICalStreamsDao" )

    private val mongoURI: MongoURI = {
        val m = new MongoURI("mongodb://127.0.0.1")
        m.getOptions.connectionsPerHost = 100
        m
    }

    private def getDatabase( dbname: String ): DB = {
        val db: DB = mongo.getDB( dbname )
        db.requestStart
        db
    }

    private val mongo: Mongo = (new Mongo.Holder()).connect( mongoURI )

    private def getCollection()(implicit dbConfig: MongoConfiguration): DBCollection =
        getDatabase( dbConfig.dbName ).getCollection( "icalstreams" )

    def findAll()(implicit dbConfig: MongoConfiguration) : List[ICalStream]= {
        val list: DBCursor = getCollection().find()
        dbFunCursor(list,List())
    }

    def save(stream:ICalStream)(implicit dbConfig:MongoConfiguration) = {
        getCollection().save(BasicDBObjectBuilder
            .start("url",stream.url)
            .append("defaultTag",stream.defaultTag)
            get)
    }

    @tailrec
    private def dbFunCursor(cursor:DBCursor , res :List[ICalStream] ) :List[ICalStream] = {
      cursor.hasNext() match {
        case true => {
            val stream: util.Map[_, _] = cursor.next().toMap
            dbFunCursor(cursor,
                ICalStream(
                stream.get("url").asInstanceOf[String],
                stream.get("defaultTag").asInstanceOf[String]) :: res)
        }
        case false => res.reverse
      }
    }
}
