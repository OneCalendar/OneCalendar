package service

import org.joda.time.DateTime

trait NowTrait {
    val now: () => Long
}
object Now extends NowTrait{
    val now: () => Long = () => DateTime.now().getMillis
}