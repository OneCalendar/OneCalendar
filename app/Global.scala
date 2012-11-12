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
                        loader.parseLoad(stream.url, stream.defaultTag)
                    } catch {
                        case e: Exception => Logger.error("something wrong with %s".format(stream.url))
                    }
            }

            Logger.trace("reload")
        }

        Akka.system.scheduler.schedule(10 seconds, 2 hours) {
          LoadDevoxx.parseLoad()
        }
    }
}
