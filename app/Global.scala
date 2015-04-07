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

import scala.concurrent.duration._
import controllers.MongoDBProdContext
import org.joda.time.DateTime
import play.api.Play.current
import play.api._
import service._
import models.ICalStream
import dao.ICalStreamDao
import play.api.libs.concurrent._
import service.{LoadICalStream}
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings with MongoDBProdContext {

    val loader: LoadICalStream = new LoadICalStream()

    override def onStart(app: Application) {
        Logger.info("start")
        Akka.system.scheduler.schedule(30 seconds, 1 day) {
            val streams: List[ICalStream] = ICalStreamDao.findAll()

            streams.foreach {
                stream =>
                    try {
                        implicit val now = () => DateTime.now.getMillis
                        loader.parseLoad(stream.url, stream.streamTags)
                    } catch {
                        case e: Exception => Logger.error("something wrong with %s : ".format(stream.url), e)
                    }
            }

            Logger.trace("reload")
        }

        Akka.system.scheduler.schedule(30 seconds, 3 hours) {
            Logger.info("Chargement devoxx")
            implicit val now = () => DateTime.now.getMillis

            try {
                LoadDevoxx.load
            } catch {
                case e: Exception => Logger.error("something wrong with load devoxx : ", e)
            }
        }

        Akka.system.scheduler.schedule(5 seconds, 1 day) { LoadEventbrite.parseLoad("scala") }
    }
}
