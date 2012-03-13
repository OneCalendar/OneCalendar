package models

import org.joda.time.DateTime

case class Event( uid: String,
                  title: String,
                  begin: DateTime,
                  end: DateTime,
                  location: String = "",
                  description: String = "",
                  tags: List[String] = Nil
)