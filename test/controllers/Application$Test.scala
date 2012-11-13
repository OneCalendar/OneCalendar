package controllers


import org.scalatest.FunSuite
import org.specs2.matcher.ShouldMatchers
import dao.EventDaoTrait
import org.mockito.Mockito.when
import models.SearchPreview
import dao.configuration.injection.MongoConfiguration
import play.api.test.Helpers._
import play.api.test._
import org.specs2.mock.Mockito
import org.mockito.Matchers
import fr.scala.util.collection.CollectionsUtils

class Application$Test extends FunSuite with ShouldMatchers with Mockito with CollectionsUtils {

  test("should find Nothing") {
    val dao = mock[EventDaoTrait]
    when(dao.findPreviewByTag(Matchers.anyListOf(classOf[String]))(any[MongoConfiguration])).thenReturn(SearchPreview(0, Seq()))
    val tags = Application.findPreviewByTags("no match")(dao)(FakeRequest())
    status(tags) should equalTo (NOT_FOUND)
  }
}