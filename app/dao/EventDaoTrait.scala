package dao

import framework.MongoConnectionProperties
import MongoConnectionProperties._
import models.Event
import models.SearchPreview
import com.mongodb.casbah.MongoDB

//TODO: this trait was added only to allow mocks into tests
trait EventDaoTrait {
    def deleteByOriginalStream(s: String)(implicit dbName: MongoDbName, pool: MongoDB, now: () => Long)

    def saveEvent(event: Event)(implicit dbName: MongoDbName, pool: MongoDB)

    def findByTag(tags: List[String])(implicit dbName: MongoDbName, pool: MongoDB): List[Event]

    def findPreviewByTag(tags: List[String])(implicit dbName: MongoDbName, pool: MongoDB, now: () => Long): SearchPreview

    def findAll()(implicit dbName: MongoDbName, pool: MongoDB): List[Event]

    def listTags()(implicit dbName: MongoDbName, pool: MongoDB, now: () => Long): List[String]
}