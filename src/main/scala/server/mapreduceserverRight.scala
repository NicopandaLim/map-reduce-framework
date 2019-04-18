package server

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}

import common._

object MapReduceApplicationRight extends App {

  val system = ActorSystem("MapReduceServerRight", ConfigFactory.load.getConfig("server2"))
  println("server ready")
}
