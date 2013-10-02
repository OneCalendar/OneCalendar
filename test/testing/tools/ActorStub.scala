package testing.tools

import akka.actor.{Actor, ActorRef}

class ActorStub(sender: ActorRef) extends Actor {
    def receive = { case message => sender ! message }
}