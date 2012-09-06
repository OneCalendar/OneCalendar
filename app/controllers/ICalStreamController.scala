package controllers

import dao.ICalStreamsDao
import models.ICalStream
import service.LoadICalStream
import play.api.mvc.Action

object ICalStreamController extends OneCalendarController{

    val loader: LoadICalStream = new LoadICalStream()

    def reloadStream() = Action {
        val streams: List[ICalStream] = ICalStreamsDao.findAll()

        streams.foreach { stream =>
            loader.parseLoad(stream.url,stream.defaultTag)
        }
        Ok("reloaded")
    }
}
