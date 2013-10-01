package arch.storage.actor

import akka.testkit.{TestActorRef, TestProbe, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import concurrent.duration._
import arch.storage.events.EventAdded
import models.Event
import org.joda.time.DateTime
import arch.storage.commands.AddEvent

class CommandActorTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                       with BeforeAndAfterAll with BeforeAndAfter with MockitoSugar with ImplicitSender {

    override def afterAll() { system.shutdown() }

    test("when CommandActor ! AddEvent should send persistenceActor ! EventAdded") {
        val persitenceStub = TestProbe()

        val commandActor = TestActorRef(new CommandActor())

        val event = Event("uuid", "title", DateTime.now(), DateTime.now().plusDays(1), "location", "description", List("tags"))
        commandActor ! AddEvent(event, 1L)

        persitenceStub.expectMsg(1 second, new EventAdded(event, 1L))
    }

    test("when CommandActor ! AddEvent should send viewActor ! EventAdded") {

    }
}