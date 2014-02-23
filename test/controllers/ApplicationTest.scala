package controllers


import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import dao.EventDaoTrait
import org.mockito.Mockito.when
import models._
import play.api.test.Helpers._
import play.api.test._
import org.specs2.mock.Mockito
import org.mockito.Matchers
import fr.scala.util.collection.CollectionsUtils
import org.joda.time.DateTime
import dao.framework.MongoConnectionProperties
import MongoConnectionProperties.MongoDbName
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads
import java.net.URLEncoder
import com.mongodb.casbah.MongoDB
import play.api.libs.json.JsSuccess
import models.SearchPreview
import api.icalendar.ICalendar
import java.io.ByteArrayInputStream

class ApplicationTest extends FunSuite with ShouldMatchers with Mockito with CollectionsUtils with PreviewJsonWriter {

    test("should find Nothing") {
        val now = () => new DateTime(2009, 1, 1, 1, 1).getMillis
        val dao = mock[EventDaoTrait]
        when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[MongoDB], any[() => Long])).thenReturn(SearchPreview(0, Seq()))
        val tags = Application.findPreviewByTags("no match")(dao, now)(FakeRequest())
        status(tags) should be(NOT_FOUND)
    }

    test("should find something with less than 3 elements") {
        val now = () => new DateTime(2009, 1, 1, 1, 1).getMillis
        val dao = mock[EventDaoTrait]
        val size: Int = 2
        when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[MongoDB], any[() => Long]))
            .thenReturn(SearchPreview(size, Seq(
            Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
            Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2")
        )))
        val events = Application.findPreviewByTags(URLEncoder.encode( "with match","UTF-8"))(dao, now)(FakeRequest())
        status(events) should be(OK)

        val result = eventListReader.reads(Json.parse(contentAsString(events)))

        result match {
            case JsSuccess(preview, _) => {
                preview.size should be (size)
                val event1 = preview.eventList.head
                event1.title should be ("title1")
                event1.begin should be (new DateTime(2010, 1, 1, 1, 1))
            }
            case JsError(errors) => fail("json n'est pas valide : " + errors)
        }


    }

    test("should find something with more than 3 elements and sort is not changed") {
        val now = () => new DateTime(2009, 1, 1, 1, 1).getMillis
        val dao = mock[EventDaoTrait]
        val size: Int = 5
        when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[MongoDB], any[() => Long]))
            .thenReturn(SearchPreview(size, Seq(
            Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
            Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2"),
            Event(uid = "y", title = "title3", begin = new DateTime(2012, 1, 1, 1, 1), end = new DateTime(2012, 1, 2, 1, 1), location = "location3")
        )))
        val tags = Application.findPreviewByTags("with match")(dao, now)(FakeRequest())

        status(tags) should be(OK)

        val result = eventListReader.reads(Json.parse(contentAsString(tags)))

        implicit val jodaOrdering = new Ordering[DateTime] {
            def compare(x: DateTime, y: DateTime): Int = x.compareTo(y)
        }

        result match {
            case JsSuccess(preview, _) => {
                preview.size should be (size)
                preview.eventList should have size (3)

                val event1 = preview.eventList.head
                event1.title should be ("title1")
                event1.begin should be (new DateTime(2010, 1, 1, 1, 1))

                val dates = preview.eventList.map(previewEvent => previewEvent.begin)
                dates should be (dates.sorted)
            }
            case JsError(errors) => fail("json n'est pas valide : " + errors)
        }
    }

    test("should find events by ids and by tags") {
        val now = () => new DateTime(2009, 1, 1, 1, 1).getMillis
        val dao = mock[EventDaoTrait]
        when(dao.findByIdsAndTags(Matchers.anyListOf(classOf[String]), Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[MongoDB], any[() => Long]))
            .thenReturn(List(
            Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
            Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2"),
            Event(uid = "y", title = "title3", begin = new DateTime(2012, 1, 1, 1, 1), end = new DateTime(2012, 1, 2, 1, 1), location = "location3")
        ))

        val tags = Application.findByIdsAndTags("fake", "fake")(dao, now)(FakeRequest())
        status(tags) should be(OK)

        val l = Json.parse(contentAsString(tags)) \\ "uid"
        l.filter(uid => uid.validate[String].getOrElse(Nil) == "Z") should have size 1
        l.filter(uid => uid.validate[String].getOrElse(Nil) == "W") should have size 1
        l.filter(uid => uid.validate[String].getOrElse(Nil) == "y") should have size 1
    }

    test("should find events by tags with ICAL format") {
        val now = () => new DateTime(2009, 1, 1, 1, 1).getMillis
        val dao = mock[EventDaoTrait]
        when(dao.findByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[MongoDB], any[() => Long]))
            .thenReturn(List(
            Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
            Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2"),
            Event(uid = "y", title = "title3", begin = new DateTime(2012, 1, 1, 1, 1), end = new DateTime(2012, 1, 2, 1, 1), location = "location3")
        ))

        val eventsResult = Application.findByTags("fake fake")(dao, now)(FakeRequest())

        status(eventsResult) should be(OK)
        val parsedFromICal = ICalendar.retrieveVEvents(new ByteArrayInputStream(contentAsBytes(eventsResult)))
        parsedFromICal match {
            case Left(cause) => fail(cause.e)
            case Right(events) => {
              events should have size (3)
                val firstEvent = events.head
                firstEvent.uid should be (Some("Z"))
                firstEvent.summary should be (Some("title1"))
                firstEvent.startDate should be (Some(new DateTime(2010, 1, 1, 1, 1)))
                firstEvent.endDate should be (Some(new DateTime(2010, 1, 2, 1, 1)))
                firstEvent.location should be (Some("location1"))
                firstEvent.url should be (None)
            }
        }
    }

    implicit val eventReader: Reads[Event] = Json.reads[Event]
    implicit val eventReaderWithEventAttribute = (__ \ "event").read(eventReader)
    implicit val eventListReader: Reads[SearchPreview] = (
        (__ \ "size").read[Long] and
            (__ \ "eventList").read(Reads.seq(eventReaderWithEventAttribute))
    )(SearchPreview)

}
