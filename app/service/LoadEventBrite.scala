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

package service

import dao.framework.MongoConnectionProperties
import MongoConnectionProperties._
import api.eventbrite.Eventbrite
import models.Event
import play.api.Logger
import dao.EventDao
import com.mongodb.casbah.MongoDB

object LoadEventbrite extends NowEventInjection {

    def parseLoad(keyWord: String)(implicit dbName: MongoDbName, pool: MongoDB) {
        val originalStream = "eventbrite-" + keyWord

        val events: Seq[Event] = Eventbrite.request(keyWord = keyWord, defaultTags = List(keyWord), originalStream = originalStream)

        val (toSave, passed) = events.partition(event => event.end.isAfter(now()))

        EventDao.deleteByOriginalStream(originalStream)

        saveEvents(toSave, originalStream)
        reportNotLoadedEvents(passed, originalStream)
    }

    private def saveEvents(toSave: Seq[Event], url: String)
                          (implicit now: () => Long, dbName: MongoDbName, pool: MongoDB) {
        toSave foreach ( EventDao.saveEvent )
        Logger.info("%d events loaded from %s".format(toSave.length, url))
    }

    private def reportNotLoadedEvents(notLoadedEvent: Seq[Event], url:String)(implicit now: () => Long) {
        if ( !notLoadedEvent.isEmpty )
            Logger.info("%d already ended events not loaded from %s".format(notLoadedEvent.length, url))
    }
}