package models

import org.joda.time.DateTime

case class Event(var dateStart: DateTime, var dateEnd: DateTime, var Tags: List[String], var place: String)