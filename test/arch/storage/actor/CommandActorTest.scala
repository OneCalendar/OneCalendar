package arch.storage.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import concurrent.duration._
import arch.storage.events.EventAdded
import models.Event
import org.joda.time.DateTime
import arch.storage.commands.AddEvent
import testing.tools.ActorStub

class CommandActorTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                       with BeforeAndAfterAll with BeforeAndAfter with MockitoSugar with ImplicitSender {

    override def afterAll() { system.shutdown() }

    test("when CommandActor ! AddEvent should send persistenceActor ! EventAdded") {
        val commandActor = TestActorRef(Props[CommandActor])
        TestActorRef(Props(new ActorStub(testActor)), "persistence")

        val now = DateTime.now()

        val event = Event("uuid", "title", now, now.plusDays(1), "location", "description", List("tags"))

        commandActor ! AddEvent(event, 1L)

        expectMsg(1 second, EventAdded("uuid", "title", now.getMillis, now.plusDays(1).getMillis,
                                       "location", "description", List("tags"), None, None, 1L))
    }

    test("when CommandActor ! AddEvent should send viewActor ! EventAdded") {

    }
}