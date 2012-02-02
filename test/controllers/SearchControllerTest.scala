package controllers

import org.scalatest.matchers.ShouldMatchers
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FunSuite}


class FirstTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  before {
    persist givenExistingEvents {
      class event1{
        def dateStart:DateTime = new DateTime(2010,1,1,20,0)
        def dateEnd:DateTime = new DateTime(2010,1,1,22,0)
        def tags = List("tag1","tag2")
        def place="La cantine"
      }
      class event2{
        def dateStart:DateTime = new DateTime(2010,1,2,20,0)
        def dateEnd:DateTime = new DateTime(2010,1,2,22,0)
        def tags = List("tag2","tag3")
        def place="Valtech"
      }
    }
    List(event1,event2)
    val events = findByTag
    events should have size 2
  }

  test("Un tag") {
    val events = findByTag("tag1")
    events should have size 1
    events should contain on "place" List("La cantine")
  }

  test("recherche qui trouvera deux elements sur un seul tag") {
    val events = findByTag("tag2")
    events should have size 2
    events should contain on "place" List("La cantine","Valtech")
  }


  test("recherche qui trouvera deux elements parce que ou sur les tags") {
    val events = findByTag("tag1 tag3")
    events should have size 2
    events should contain on "place" List("La cantine","Valtech")
  }

  after {
    emptyBeans
    val events = findByTag
    events should have size 0
  }
}
