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
import dao.framework.MongoDbModel

case class ICalStream (url: String, streamTags: List[String])

trait ICalStreamMongoMapper {
    implicit object ICalStreamMongoModel extends MongoDbModel[ICalStream] {
        def collectionName: String = "icalstreams"

        def write(stream: ICalStream): DBObject =
            DBObject( "url" -> stream.url, "streamTags" -> stream.streamTags )

        def read(dbo: DBObject): ICalStream =
            ICalStream(
                url = dbo.as[String]("url"),
                streamTags = dbo.as[MongoDBList]("streamTags").toList.asInstanceOf[List[String]]
            )
    }
}