package arch.storage.actor

import akka.actor.{ActorLogging, Actor}
import arch.storage.events.EventAdded
import arch.storage.commands.AddEvent

class CommandActor extends Actor with ActorLogging {

    private val system = context.system
    private lazy val persistenceActor = system.actorSelection(system / "persistence")
    //private lazy val viewActor = context.system.actorSelection(context.system / "view")

    def receive = {
        case AddEvent(event, timestamp) => persistenceActor ! new EventAdded(event, timestamp)
        case message => log.warning(s"unknown message received ! : $message")
    }
}