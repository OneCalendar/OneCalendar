package dao

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, FunSuite}
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}
import dao.EventRepositoryBis._
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
        val sinceDate = new DateTime(2010, 1, 1, 1, 1)

        initData(eventDevoxx, eventJava, eventOther, event4, newEvent)

        // When
        val fResult = SearchPreviewDao.findPreviewByTag(Set("devoxx", "java", "other"), sinceDate)

        // Then
        val result = Await.result(fResult, 2 seconds)

        result should have size 4
        result.eventList.foreach { List(eventJava, eventDevoxx, eventOther, event4) should contain (_) }
    }

    test("should not return past events") {
        val sinceDate = DateTime.now()
        initData(newEvent, oldEvent)

        // When
        val fResult = SearchPreviewDao.findPreviewByTag(Set("new", "4", "other"), sinceDate)

        // Then
        val result = Await.result(fResult, 2 seconds)

        result.size shouldBe 1
        result.eventList should have size 1
        result.eventList.foreach( _.begin.compareTo(sinceDate) should be >= 1 )
        result.eventList.foreach( _.tags shouldBe List("NEW") )
    }
}