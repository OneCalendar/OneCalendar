package dao

import models.SearchPreview
import org.joda.time.DateTime

import scala.concurrent.Future

trait SearchPreviewDao {
	def findPreviewByTag(tags: Set[String], sinceDate: DateTime): Future[SearchPreview]

}