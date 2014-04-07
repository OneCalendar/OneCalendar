package dao

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, FunSuite}
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}
import dao.EventRepository._
import reactivemongo.api.{MongoDriver, DB}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import org.joda.time.DateTime


class SearchPreviewDaoTest extends FunSuite with Matchers with BeforeAndAfterAll with BeforeAndAfter
    with MongoEmbedDatabase with DatabaseUtils {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27022) }
    override def afterAll() { mongoStop(mongoProps) }

    before { drop() }

    implicit val connection: DB = {
        val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27022"))
        connection("test")
    }

    // utile pour les méthodes de DatabaseUtils
    implicit val eventsColl = connection[JSONCollection]("events")

    test("should find 4 first events by tags 'devoxx', 'java' or other ") {
        implicit val now = new DateTime(2010, 1, 1, 1, 1)

        initData(eventDevoxx, eventJava, eventOther, event4, newEvent)

        // When
        val fResult = SearchPreviewDao.findPreviewByTag(Set("devoxx", "java", "other"))

        // Then
        val result = Await.result(fResult, 2 seconds)

        result should have size 4
        result.eventList shouldBe List(eventJava, eventDevoxx, eventOther, event4)
    }

    test("should not return past events") {
        implicit val now = new DateTime(2012, 4, 20, 0, 0, 0, 0)

        initData(eventDevoxx, eventJava, eventOther, event4)

        // When
        val fResult = SearchPreviewDao.findPreviewByTag(Set("devoxx", "java", "other"))

        // Then
        val result = Await.result(fResult, 2 seconds)

        result.size shouldBe 3
        result.eventList should have size 3
        result.eventList.foreach( _.begin.compareTo(now) should be >= 1 )
    }
}