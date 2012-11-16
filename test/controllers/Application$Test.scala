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
import com.codahale.jerkson.Json
import dao.configuration.injection.MongoProp.MongoDbName

class Application$Test extends FunSuite with ShouldMatchers with Mockito with CollectionsUtils {

  test("should find Nothing") {
    val now = () => new DateTime(2009,1,1,1,1).getMillis
    val dao = mock[EventDaoTrait]
    when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[() => Long])).thenReturn(SearchPreview(0, Seq()))
    val tags = Application.findPreviewByTags("no match")(dao,now)(FakeRequest())
    status(tags) should be (NOT_FOUND)
  }

  test("should find something with less than 3 elements") {
      val now = () => new DateTime(2009,1,1,1,1).getMillis
    val dao = mock[EventDaoTrait]
    val size: Int = 2
    when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[() => Long]))
      .thenReturn(SearchPreview(size, Seq(
      Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
      Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2")
    )))
    val tags = Application.findPreviewByTags("with match")(dao,now)(FakeRequest())
    status(tags) should be (OK)

    val currentPreview = Json.parse[Preview](contentAsString(tags))
    currentPreview.size should be (size)
    currentPreview.eventList should have size (size)

    val event1: PreviewEvent = currentPreview.eventList.head
    event1.event.title should be ("title1")
    event1.event.date should be ("2010-01-01T01:01:00.000+01:00")
  }

  test("should find something with more than 3 elements and sort is not changed") {
    val now = () => new DateTime(2009,1,1,1,1).getMillis
    val dao = mock[EventDaoTrait]
    val size: Int = 5
    when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoDbName], any[() => Long]))
      .thenReturn(SearchPreview(size, Seq(
      Event(uid = "Z", title = "title1", begin = new DateTime(2010, 1, 1, 1, 1), end = new DateTime(2010, 1, 2, 1, 1), location = "location1"),
      Event(uid = "W", title = "title2", begin = new DateTime(2011, 1, 1, 1, 1), end = new DateTime(2011, 1, 2, 1, 1), location = "location2"),
      Event(uid = "y", title = "title3", begin = new DateTime(2012, 1, 1, 1, 1), end = new DateTime(2012, 1, 2, 1, 1), location = "location3")
    )))
    val tags = Application.findPreviewByTags("with match")(dao,now)(FakeRequest())
    status(tags) should be (OK)

    val currentPreview = Json.parse[Preview](contentAsString(tags))
    currentPreview.size should be (size)
    currentPreview.eventList should have size (3)

    val event1: PreviewEvent = currentPreview.eventList.head
    event1.event.title should be ("title1")
    event1.event.date should be ("2010-01-01T01:01:00.000+01:00")

    val dates = currentPreview.eventList.map(eventTuple => eventTuple.event.date)

    dates should be (dates.sorted)
  }
}