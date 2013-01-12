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

package models

import com.mongodb.casbah.Imports._
import dao.MongoDbModel
import org.joda.time.DateTime
import util.matching.Regex

object Event {
    //TODO explicit regex
    val TAG_PATTERN : String = """#([\w\d\p{L}]+)"""

    def getTagsFromDescription(s: String): List[String] =
        new Regex(TAG_PATTERN).findAllIn(s).map(_.replace("#", "").toUpperCase.trim()).toList

    def getDescriptionWithoutTags(s: String):String = {
        val description : String = s.replaceAll(TAG_PATTERN,"")
        description.trim()
    }
}

case class Event( uid: String = "",
                  title: String = "",
                  begin: DateTime,
                  end: DateTime,
                  location: String = "",
                  description: String = "",
                  tags: List[String] = Nil,
                  originalStream: Option[String] = None,
                  url:Option[String] = None
                 )

trait EventTypeClass {
    implicit object EventMongoModel extends MongoDbModel[Event] {
        def collectionName: String = "events"

        def write(event: Event): DBObject =
            DBObject(
                "begin" -> event.begin.getMillis,
                "description" -> event.description,
                "end" -> event.end.getMillis,
                "location" -> event.location,
                "originalStream" -> event.originalStream,
                "tags" -> event.tags,
                "title" -> event.title,
                "uid" -> event.uid,
                "url" -> event.url
            )

        def read(dbo: DBObject): Event =
            Event(
                begin = new DateTime(dbo.as[Long]("begin")),
                description = dbo.as[String]("description"),
                end = new DateTime(dbo.as[Long]("end")),
                location = dbo.as[String]("location"),
                originalStream = dbo.getAs[String]("originalStream"),
                tags = dbo.as[MongoDBList]("tags").toList.asInstanceOf[List[String]],
                title = dbo.as[String]("title"),
                uid = dbo.getAs[String]("uid").getOrElse(""),
                url = dbo.getAs[String]("url")
            )
    }
}