package service

import models._
import org.scalatest.matchers._
import org.joda.time.DateTime
import org.scalatest._


class SearchTest extends FunSuite
  with ShouldMatchers
  with BeforeAndAfter
  with MustMatchers {


  before {
    val event1 = Event(new DateTime(2010,1,1,20,0),new DateTime(2010,1,1,22,0),List("tag1","tag2"),"La cantine")
    val event2 = Event(new DateTime(2010,1,2,20,0),new DateTime(2010,1,2,22,0),List("tag2","tag3"),"Valtech")
    PersistService.persist(List(event1,event2))
    val events = FindService.findByTag("")
    events must have length (2)
  }

  test("Un tag") {
    val events = FindService.findByTag("tag1")
    events must have length (1)
    events.map(_.place) must be(List("La cantine"))
  }

  test("recherche qui trouvera deux elements sur un seul tag") {
    val events = FindService.findByTag("tag2")
    events must have length (2)
    events.map(_.place) should be(List("La cantine","Valtech"))
  }


  test("recherche qui trouvera deux elements parce que ou sur les tags") {
    val events = FindService.findByTag("tag1 tag3")
    events must have length (2)
    events.map(_.place) should be(List("La cantine","Valtech"))
  }

  after {
    PersistService.emptyBeans _
    val events =FindService.findByTag("")
    events must have length (0)
  }
}
