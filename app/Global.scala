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

import controllers.OneCalendarController
import play.api._
import service.{LoadDevoxx, LoadICalStream}
import models.ICalStream
import dao.ICalStreamsDao
import play.api.libs.concurrent._
import play.api.Play.current
import akka.util.duration._

object Global extends GlobalSettings with OneCalendarController {

    val loader: LoadICalStream = new LoadICalStream()

    override def onStart(app: Application) {
        Logger.info("start")
        Akka.system.scheduler.schedule(30 seconds, 1 day) {
            val streams: List[ICalStream] = ICalStreamsDao.findAll()

            streams.foreach {
                stream =>
                    try {
                        loader.parseLoad(stream.url, stream.streamTags)
                    } catch {
                        case e: Exception => Logger.error("something wrong with %s : ".format(stream.url) + e.getMessage)
                    }
            }

            Logger.trace("reload")
        }

        Akka.system.scheduler.schedule(10 seconds, 2 hours) {
          LoadDevoxx.parseLoad()
        }
    }
}
