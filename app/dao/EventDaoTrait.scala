package dao

import models.Event
import com.mongodb.casbah.Imports._
import models.SearchPreview

//ToDo: this trait was added only to allow mocks into tests
trait EventDaoTrait {

  def deleteByOriginalStream(s: String)(implicit collection : String => MongoCollection, now: () => Long)

  def saveEvent(event: Event)(implicit collection : String => MongoCollection)

  def findByTag(tags: List[String])(implicit collection : String => MongoCollection): List[Event]

  def findPreviewByTag(tags: List[String])(implicit collection : String => MongoCollection, now: () => Long): SearchPreview

  def findAll()(implicit collection : String => MongoCollection): List[Event]

  def listTags()(implicit collection : String => MongoCollection, now: () => Long): List[String]
}