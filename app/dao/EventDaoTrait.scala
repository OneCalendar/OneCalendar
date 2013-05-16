package dao

import configuration.injection.MongoProp._
import models.Event
import models.SearchPreview

//TODO: this trait was added only to allow mocks into tests
trait EventDaoTrait {
    def deleteByOriginalStream(s: String)(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long)

    def saveEvent(event: Event)(implicit dbName: MongoDbName, port: MongoDbPort = 27017)

    def findByTag(tags: List[String])(implicit dbName: MongoDbName, port: MongoDbPort = 27017): List[Event]

    def findPreviewByTag(tags: List[String])(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): SearchPreview

    def findAll()(implicit dbName: MongoDbName, port: MongoDbPort = 27017): List[Event]

    def listTags()(implicit dbName: MongoDbName, port: MongoDbPort = 27017, now: () => Long): List[String]
}