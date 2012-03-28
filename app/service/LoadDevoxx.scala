package service

import dao.configuration.injection._
import dao.configuration.injection.MongoConfiguration._
import com.codahale.jerkson.Json
import dao._
import models._
import java.net._

class LoadDevoxx extends Json {


    val DB_NAME : String = "OneCalendar"

    def parseLoad() ( implicit dbConfig: MongoConfiguration = MongoConfiguration( DB_NAME ) ) {

        EventDao.deleteAll()

        val schedules: Seq[DevoxxSchedule] = parse[Seq[DevoxxSchedule]](new URL("https://cfp.devoxx.com/rest/v1/events/6/schedule").openStream)

        schedules.foreach(schedule => {
            val presentation: DevoxxPresentation = parse[DevoxxPresentation](new URL(schedule.presentationUri.get).openStream())

//              var event:Event = new Event(schedule.id,presentation.title,presentation,schedul,Nil,);
//            presentation.
        })
    }

}