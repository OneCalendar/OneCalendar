package dao

import dao.configuration.injection.MongoProp.MongoDbName
import models.Event
import models.SearchPreview

//ToDo: this trait was added only to allow mocks into tests
trait EventDaoTrait {

  def deleteByOriginalStream(s: String)(implicit dbName: MongoDbName, now: () => Long)

  def saveEvent(event: Event)(implicit dbName: MongoDbName)

  def findByTag(tags: List[String])(implicit dbName: MongoDbName): List[Event]

  def findPreviewByTag(tags: List[String])(implicit dbName: MongoDbName, now: () => Long): SearchPreview

  def findAll()(implicit dbName: MongoDbName): List[Event]

  def listTags()(implicit dbName: MongoDbName, now: () => Long): List[String]
}