package dao

import com.github.simplyscala.MongodProps
import dao.EventRepositoryBis._
import dao.connection.MongoDbConnection
import models.EventJsonFormatter._
import org.joda.time.DateTime
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{DB, MongoDriver}
import testutils.MongoTestSuite

import scala.concurrent.Await
import scala.concurrent.duration._


class MongoDbSearchPreviewDaoTest extends MongoTestSuite {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27022) }
    override def afterAll() { mongoStop(mongoProps) }

    before { drop(eventsColl) }

	trait TestMongoDbConnection extends MongoDbConnection { val db = connection }
	object UnderTestDao extends MongoDbSearchPreviewDao with TestMongoDbConnection


	val connection: DB = {
        val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27022"))
        connection("test")
    }

    val eventsColl = connection[JSONCollection]("events")

    test("should find 4 first events by tags 'devoxx', 'java' or other ") {
        val sinceDate = new DateTime(2010, 1, 1, 1, 1)

        initDataz(eventsColl, eventDevoxx, eventJava, eventOther, event4, newEvent)

        // When
        val fResult = UnderTestDao.findPreviewByTag(Set("devoxx", "java", "other"), sinceDate)

        // Then
        val result = Await.result(fResult, 2 seconds)

        result should have size 4
        result.eventList.foreach { List(eventJava, eventDevoxx, eventOther, event4) should contain (_) }
    }

    test("should not return past events") {
        val sinceDate = DateTime.now()
        initDataz(eventsColl, newEvent, oldEvent)

        // When
        val fResult = UnderTestDao.findPreviewByTag(Set("new", "4", "other"), sinceDate)

        // Then
        val result = Await.result(fResult, 2 seconds)

        result.size shouldBe 1
        result.eventList should have size 1
        result.eventList.foreach( _.begin.compareTo(sinceDate) should be >= 1 )
        result.eventList.foreach( _.tags shouldBe List("NEW") )
    }
}