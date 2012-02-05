package controllers

import models._
import controllers._
import org.scalatest.matchers.ShouldMatchers
import org.joda.time.DateTime
import org.scalatest._


object FirstTest extends FunSuite with ShouldMatchers with BeforeAndAfter {


  before {
    val event1 = Event(new DateTime(2010,1,1,20,0),new DateTime(2010,1,1,22,0),List("tag1","tag2"),"La cantine")
    val event2 = Event(new DateTime(2010,1,2,20,0),new DateTime(2010,1,2,22,0),List("tag2","tag3"),"Valtech")
    PersistController.persist(event1,event2)
    val events = FindController.findByTag
    events should have size 2
  }

  test("Un tag") {
    val events = FindController.findByTag("tag1")
    events should have size 1
    events(1) should have place "La cantine"
  }

  test("recherche qui trouvera deux elements sur un seul tag") {
    val events = FindController.findByTag("tag2")
    events should have size 2
    events(1) should have place "La cantine"
    events(2) should have place "Valtech"
  }


  test("recherche qui trouvera deux elements parce que ou sur les tags") {
    val events = FindController.findByTag("tag1 tag3")
    events should have size 2
    events(1) should have place "La cantine"
    events(2) should have place "Valtech"
  }

  after {
    PersistController.emptyBeans
    val events =controllers. FindController.findByTag
    events should have size 0
  }
}
