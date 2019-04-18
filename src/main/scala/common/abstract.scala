package common

import akka.actor.{ActorSystem, Props, ActorRef}
import scala.collection.mutable.HashMap

@SerialVersionUID(100L)
trait Mapred extends Serializable{	

	def mapFunction(k1: String, v1:String, reduceActors: ActorRef)
	def redFunction(k2: String, v2:String, reduceMap : HashMap[String, List[String]]): HashMap[String, List[String]]
}

 