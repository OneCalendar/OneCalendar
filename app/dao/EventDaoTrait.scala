package dao

import framework.MongoConnectionProperties
import MongoConnectionProperties._
import models.Event
import models.SearchPreview
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.Imports._
import models.SearchPreview

// this trait was added only to allow mocks into tests
trait EventDaoTrait {
    def deleteByOriginalStream(s: String)(implicit dbName: MongoDbName, connection: MongoDB, now: () => Long)

    def saveEvent(event: Event)(implicit dbName: MongoDbName, connection: MongoDB)

    def findByTag(tags: List[String])(implicit dbName: MongoDbName, connection: MongoDB, now: () => Long): List[Event]

    def findPreviewByTag(tags: List[String])
                        (implicit dbName: MongoDbName, connection: MongoDB, now: () => Long): SearchPreview

    def findAll()(implicit dbName: MongoDbName, connection: MongoDB): List[Event]

    def listTags()(implicit dbName: MongoDbName, connection: MongoDB, now: () => Long): List[String]

	def findByIdsAndTags(ids: List[String], tags: List[String])(implicit dbName: MongoDbName, connection: MongoDB, now: () => Long): List[Event]

}