package dao

import com.github.simplyscala.MongodProps
import dao.EventRepositoryBis._
import dao.connection.MongoDbConnection
import models.Event
import models.EventJsonFormatter._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{DB, MongoDriver}
import testutils.MongoTestSuite
import scala.concurrent.Await
import scala.concurrent.duration._

class MongoDbEventDaoTest extends MongoTestSuite {

    var mongoProps: MongodProps = null
    override def beforeAll() { mongoProps = mongoStart(27033) }
    override def afterAll() { mongoStop(mongoProps) }

    before { drop(eventsColl) }

    val connection: DB = {
        val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27033"))
        connection("test")
    }

    val eventsColl = connection[JSONCollection]("events")

	trait TestMongoDbConnection extends MongoDbConnection { val db = connection }
	object UnderTestDao extends MongoDbEventDao with TestMongoDbConnection

    test("saving a new event") {
        val event: Event = Event(
            uid = "1",
            title = "BOF",
            begin = new DateTime(2012, 4, 19, 0, 0, 0, 0),
            end = new DateTime(2012, 4, 19, 0, 0, 0, 0),
            description = "",
            location = "",
            tags = List("JAVA", "DEVOXX")
        )

        // When
        Await.ready(UnderTestDao.saveEvent(event), 2 seconds)

        // Then
        val result = eventsColl.find(Json.obj()).cursor[Event].collect[List]()
        Await.result(result, 2 seconds) shouldBe List(event)
    }

    test("should find event by tag 'devoxx'") {
        initDataz(eventsColl, eventDevoxx, eventJava)

        // When
        val result = UnderTestDao.findByTags(Set("devoxx"), new DateTime(2010, 1, 1, 1, 1))

        // Then
        Await.result(result, 2 seconds) shouldBe Set(eventDevoxx)
    }

    test("should find events by tags 'devoxx' or 'java' ") {
        initDataz(eventsColl, eventDevoxx, eventJava)

        // When
        val result = UnderTestDao.findByTags(Set("devoxx", "java"), new DateTime(2010, 1, 1, 1, 1))

        // Then
        Await.result(result, 2 seconds) shouldBe Set(eventDevoxx, eventJava)
    }

    test("should find all events from now") {
        initDataz(eventsColl, oldEvent, newEvent)

        Await.result(UnderTestDao.findAllFromNow(), 2 seconds) shouldBe Set(newEvent)
    }

    test("count future events") {
        initDataz(eventsColl, oldEvent, newEvent)

        Await.result(UnderTestDao.countFutureEvents(), 2 seconds) shouldBe 1
    }

    test("should list tags") {
        initDataz(eventsColl, eventDevoxx, eventJava)

        Await.result(UnderTestDao.listTags(), 2 seconds) shouldBe Set("DEVOXX", "JAVA")
    }

    test("should not list old tags") {
        initDataz(eventsColl, oldEvent, newEvent)

        Await.result(UnderTestDao.listTags(), 2 seconds) shouldBe Set("NEW")
    }

    test("delete by originalStream will drop all") {
        val e1 = Event(
            originalStream = Option("hello"),
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1", "tag2")
        )

        val e2 = Event(
            originalStream = Option("hello"),
            begin = new DateTime().plusDays(10),
            end = new DateTime().plusDays(10),
            title = "title2",
            description = "description2",
            tags = List("tag1", "tag2")
        )

        val e3 = Event(
            originalStream = Option("hello"),
            begin = new DateTime().minusDays(10),
            end = new DateTime().minusDays(10),
            title = "title",
            description = "description",
            tags = List("tag1", "tag2")
        )

        initDataz(eventsColl, e1,e2,e3)
        initDataz(eventsColl, eventDevoxx, eventJava)


        Await.result(UnderTestDao.countFutureEvents(new DateTime(2010, 5, 1, 1, 1)), 2 seconds) shouldBe 5

        // When
        UnderTestDao.deleteByOriginalStream("hello")

        // Then
        Await.result(UnderTestDao.countFutureEvents(new DateTime(2010, 5, 1, 1, 1)), 2 seconds) shouldBe 2
    }

    // TODO WHY THIS TEST ? for tstting option mapping ???
    ignore("should find event even if it have not originalStream and url") {
        /*implicit val now: () => Long = () => new DateTime(2010, 1, 1, 1, 1).getMillis
        val eventWithNoOrigStreamAndUrl =
            Event("uid", "title", DateTime.now().plusDays(1), DateTime.now().plusDays(2), "location", "description", tags = List("TEST"))

        EventDao.saveEvent(eventWithNoOrigStreamAndUrl)

        EventDao.findByTag(List("test")) should be(List(eventWithNoOrigStreamAndUrl))
        EventDao.findAll() should be(List(eventWithNoOrigStreamAndUrl))*/
    }

    // TODO pass only by EventDao to store Event case class, then uid = null it is not possible, or uis become Option[String] instead of String
    ignore("should not fail when event found without uid but which is in database") {
        /*val now: DateTime = new DateTime(2012, 1, 1, 1, 1)
        //implicit val fnow: () => Long = () => new DateTime(2010, 1, 1, 1, 1).getMillis

        EventDao.saveEvent(Event(uid = null, tags = List("NO_UID"), begin = now, end = now))

        EventDao.findByTag(List("NO_UID")) should be(List(Event(uid = "", tags = List("NO_UID"), begin = now, end = now)))*/
    }
}

object EventRepositoryBis {
    val eventDevoxx: Event = Event(
        uid = "1",
        title = "BOF",
        begin = DateTime.now().plusDays(1),
        end = DateTime.now().plusDays(2),
        tags = List("DEVOXX")
    )

    val eventJava: Event = Event(
        uid = "2",
        title = "BOF",
        begin = DateTime.now().plusDays(1),
        end = DateTime.now().plusDays(2),
        tags = List("JAVA")
    )

    val eventOther: Event = Event(
        uid = "3",
        title = "BOF",
        begin = new DateTime(2012, 4, 21, 15, 0, 0, 0),
        end = new DateTime(2012, 4, 21, 16, 0, 0, 0),
        tags = List("OTHER")
    )

    val event4: Event = Event(
        uid = "4",
        title = "BOF",
        begin = new DateTime(2012, 4, 21, 15, 0, 0, 0),
        end = new DateTime(2012, 4, 21, 16, 0, 0, 0),
        tags = List("4", "OTHER")
    )

    val oldEvent: Event = Event(
        uid = "4",
        title = "BOF",
        begin = new DateTime().minusDays(2),
        end = new DateTime().minusDays(2),
        tags = List("4", "OTHER")
    )

    val newEvent: Event = Event(
        uid = "NEW",
        title = "NEW",
        begin = new DateTime().plusDays(1),
        end = new DateTime().plusDays(1),
        tags = List("NEW")
    )
}