package dao

import configuration.injection.MongoConfiguration
import models.SearchPreview
import models.Event

trait EventDaoTrait {
  def deleteAll()(implicit dbConfig: MongoConfiguration)

  def deleteByOriginalStream(s: String)(implicit dbConfig: MongoConfiguration)

  def saveEvent(event: Event)(implicit dbConfig: MongoConfiguration)

  def findByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): List[Event]

  def findPreviewByTag(tags: List[String])(implicit dbConfig: MongoConfiguration): SearchPreview

  def findAll()(implicit dbConfig: MongoConfiguration): List[Event]

  def listTags()(implicit dbConfig: MongoConfiguration): List[String]
}