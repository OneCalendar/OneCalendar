package models

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import models.Event._

class EventTest extends FunSuite with ShouldMatchers {

    test("should get tags from description"){
        val tags : List[String] = getTagsFromDescription("bla bla bla #Toto #tiTI ")

        tags.size should  be (2)
        tags(0) should be ("TOTO")
        tags(1) should be ("TITI")
    }

    test("should remove tags from event description"){
        getDescriptionWithoutTags("bla bla bla bla bla #toto") should be ("bla bla bla bla bla")
        getDescriptionWithoutTags("bla bla bla bla bla #toto #titi #tata") should be ("bla bla bla bla bla")
        getDescriptionWithoutTags("bla bla bla bla bla") should be ("bla bla bla bla bla")
    }
}