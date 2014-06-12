package dao

import models.Event
import org.joda.time.DateTime
import reactivemongo.core.commands.LastError

import scala.concurrent.Future

trait EventDao {
	def saveEvent(event: Event): Future[LastError]

	def findByTags(tags: Set[String], sinceDate: DateTime = DateTime.now()): Future[Set[Event]]

	def findAllFromNow(): Future[Set[Event]]

	def listTags(sinceDate: DateTime = DateTime.now()): Future[Set[String]]

	def countFutureEvents(sinceDate: DateTime = DateTime.now()): Future[Int]

	def deleteByOriginalStream(originalStream: String): Future[LastError]
}