package arch.storage.commands

import models.Event

trait EventCommand
case class AddEvent(event: Event, timestamp: Long) extends EventCommand
case class RemoveEvent(event: Event, timestamp: Long) extends EventCommand
case class UpdateEvent(event: Event, timestamp: Long) extends EventCommand