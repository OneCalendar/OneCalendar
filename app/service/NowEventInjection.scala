package service

import org.joda.time.DateTime
trait NowEvent {
    implicit val now: () => Long
}
trait NowEventInjection extends NowEvent{
    override implicit val now: () => Long = () => DateTime.now().getMillis
}