package common
import akka.routing.ConsistentHashingRouter.ConsistentHashable


case class InputPair(k1: String, v1: String)    
case class Intermediate(k2: String, v2: String)

case object Flush
case object Done


