package client

import akka.actor.{Actor, Props, ActorRef, Address, OneForOneStrategy, Terminated}
import akka.routing.{Broadcast, RoundRobinPool, ConsistentHashingPool}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.remote.routing.RemoteRouterConfig
import akka.event.Logging
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap

import com.typesafe.config.ConfigFactory

import common._ 

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}

class MasterActor(mapred: Mapred) extends Actor {  
  
  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception                => Resume
  }

  val numberMappers  = ConfigFactory.load.getInt("number-mappers")      
  val numberReducers  = ConfigFactory.load.getInt("number-reducers")    

  var pending = numberReducers

  val addresses = Seq(
    //Address("akka", "ReverseIndex2"),
    Address("akka.tcp", "MapReduceServerLeft", "127.0.0.1", 2552),
    Address("akka.tcp", "MapReduceServerRight", "127.0.0.1", 2553)      
  )

  def hashMapping: ConsistentHashMapping = { 
    case Intermediate(k2, val2) => k2     
  }
  
  // Mappers should be produced after Reducers, coz reducers as input in Mappers producing
  val reduceActors = context.actorOf(RemoteRouterConfig(ConsistentHashingPool(numberReducers, supervisorStrategy = supervisorStrategy, hashMapping = hashMapping), addresses).props(Props(classOf[ReduceActor], mapred)))
  val mapActors = context.actorOf(RemoteRouterConfig(RoundRobinPool(numberMappers), addresses).props(Props(classOf[MapActor], reduceActors, mapred)))       // plug in map functions here
  
  def receive = {
    case msg: InputPair =>     
      mapActors ! msg
    case Flush =>
      mapActors ! Broadcast(Flush)
    case Done =>
      println("Received Done from" + sender)
      pending -= 1
      if (pending == 0)
        context.system.terminate
 
  }

}
