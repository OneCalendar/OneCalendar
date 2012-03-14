package service

import models.Event
import dao.configuration.injection.MongoConfiguration

class CalendarStream {

    def search(tags: List[String])(implicit dbConfig: MongoConfiguration): List[Event] = {
        if (tags.size > 0) {
            dao.EventDao.findByTag(tags)
        } else {
            dao.EventDao.findAll()
        }
    }
}