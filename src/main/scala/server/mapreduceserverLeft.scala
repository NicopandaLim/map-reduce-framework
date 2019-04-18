package server

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}

import common._

object MapReduceApplicationLeft extends App {

  val system = ActorSystem("MapReduceServerLeft", ConfigFactory.load.getConfig("server1"))
  println("server ready")
}
