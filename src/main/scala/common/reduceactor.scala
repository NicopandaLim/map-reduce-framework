package common

import scala.collection.mutable.HashMap
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.stream.actor.{ActorSubscriber, MaxInFlightRequestStrategy}


class ReduceActor(mapred: Mapred) extends Actor { 
  
  println(self.path)

  Thread sleep 2000

  var remainingMappers = ConfigFactory.load.getInt("number-mappers")
  var reduceMap = HashMap[String, List[String]]()
  var flag = false
 
  def receive = {
    
    case Intermediate(k2, val2) =>
      if (val2.equals("one")) {flag = true}
      reduceMap = mapred.redFunction(k2, val2, reduceMap)
    
    case Flush =>
      remainingMappers -= 1
      if (remainingMappers == 0) {
        if (flag){
          for((k,v)<- reduceMap) {println(k + "->"+ v.length)}
        }else{
          println(self.path.toStringWithoutAddress + " : " + reduceMap)
        }
        context.actorSelection("../..") ! Done
//        context stop self
      }
  }
}
