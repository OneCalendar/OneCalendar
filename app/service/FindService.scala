package service

import models._
import org.joda.time.DateTime

object FindService {
  def findByTag(tags:String = ""):List[Event] = {
    List(new Event(new DateTime(),new DateTime(),List(""),"nowhere"))
  }
}
