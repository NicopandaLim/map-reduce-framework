package common

import scala.collection.mutable.HashMap
import akka.actor.{Actor, ActorRef}
import akka.routing.Broadcast


class MapActor(reduceActors: ActorRef, mapred: Mapred) extends Actor{

  override def preStart() {println("preStart")}

  override def postStop() {println("postStop")}

  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("preRestart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    println("postRestart")
    super.postRestart(reason)
  }

  println("Constructor")

  println(self.path)

  Thread sleep 2000

  def receive = {
    case InputPair(k1, val1)  =>
      mapred.mapFunction(k1, val1, reduceActors)     
    case Flush => 
      reduceActors ! Broadcast(Flush) 
  }
}
