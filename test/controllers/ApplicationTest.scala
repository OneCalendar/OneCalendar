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
import dao.configuration.injection.MongoPoolProperties.MongoDbName
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads
import java.net.URLEncoder
import com.mongodb.casbah.MongoDB

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
        val tags = Application.findPreviewByTags(URLEncoder.encode( "with match","UTF-8"))(dao, now)(FakeRequest())
        status(tags) should be(OK)

        val result = previewReader.reads(Json.parse(contentAsString(tags)))

        result match {
            case JsSuccess(preview, _) => {
                preview.size should be (size)
                val event1 = preview.eventList.head
                event1.title should be ("title1")
                event1.date should be ("2010-01-01T01:01:00.000+01:00")
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

        val result = previewReader.reads(Json.parse(contentAsString(tags)))

        result match {
            case JsSuccess(preview, _) => {
                preview.size should be (size)
                preview.eventList should have size (3)

                val event1 = preview.eventList.head
                event1.title should be ("title1")
                event1.date should be ("2010-01-01T01:01:00.000+01:00")

                val dates = preview.eventList.map(previewEvent => previewEvent.date)
                dates should be (dates.sorted)
            }
            case JsError(errors) => fail("json n'est pas valide : " + errors)
        }
    }

    implicit val previewEventReader: Reads[PreviewEvent] = Json.reads[PreviewEvent]
    implicit val previewEventReaderWithEventTag = (__ \ "event").read(previewEventReader)
    implicit val previewReader: Reads[Preview] = (
        (__ \ "size").read[Long] and
            (__ \ "eventList").read(Reads.seq(previewEventReaderWithEventTag))
    )(Preview)
}