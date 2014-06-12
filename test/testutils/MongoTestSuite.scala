package testutils

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, FunSuite}
import com.github.simplyscala.MongoEmbedDatabase
import scala.concurrent.{Future, Await}
import play.api.libs.json.Writes
import reactivemongo.api.collections.default.BSONCollection

trait MongoTestSuite extends FunSuite with Matchers with BeforeAndAfterAll with BeforeAndAfter
			with MongoEmbedDatabase with DatabaseUtils {
	import concurrent.ExecutionContext.Implicits.global
	protected implicit val EC = global
}

trait DatabaseUtils {
	import scala.concurrent.ExecutionContext.Implicits.global
	import scala.concurrent.duration._
	import play.modules.reactivemongo.json.collection.JSONCollection
	import concurrent.Await.result

	def sync[T](fThings: Future[T], duration: Duration = 2 seconds): T = result(fThings, duration)

	def drop(collections: JSONCollection*) = collections.foreach { coll => Await.ready(coll.drop(), 2 seconds) }

	def dropBson(collections: BSONCollection*) = collections.foreach { coll => Await.ready(coll.drop(), 2 seconds) }

	def initDataz[T](jsonCollection: JSONCollection, dataz: T*)(implicit writer: Writes[T]) = {
		dataz.foreach { data => Await.ready(jsonCollection.save(data), 2 seconds) }
	}
}