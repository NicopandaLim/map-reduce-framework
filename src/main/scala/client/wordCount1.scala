package client

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props, ActorRef}
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.io.Source

import common._
import client._

import java.util.ArrayList
import java.util.StringTokenizer

class WordCount extends Mapred{  

  override def mapFunction (k:String, text: String, reduceActors: ActorRef) = {
    val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
    System.out.println(text)
    var parser: StringTokenizer = new StringTokenizer(text)
  while (parser.hasMoreTokens()) {
    var word: String = parser.nextToken().toLowerCase()
    
    if (!STOP_WORDS_LIST.contains(word)) {
        reduceActors ! Intermediate(word,"one")
      }
    }
  }

  def redFunction(word: String, cnt: String, reduceMap : HashMap[String, List[String]]): HashMap[String, List[String]] = {
    if (reduceMap.contains(word)) {
        reduceMap += (word -> (cnt :: reduceMap(word)))
      }else{
        reduceMap += (word -> List(cnt))
      }

    return reduceMap  
  }
}


object WordCount1 extends App {
  
  val wordCnt = new WordCount()
  val system = ActorSystem("WordCount1", ConfigFactory.load.getConfig("client"))
  val master = system.actorOf(Props(classOf[MasterActor], wordCnt), name = "master")
  
  master ! InputPair("example1", "There were a king with a large jaw and a queen with a plain face on the throne of England there were a king with a large jaw and a queen with a fair face on the throne of France In both countries it was clearer than crystal to the lords of the State preserves of loaves and fishes that things in general were settled for ever")
  master ! InputPair("example2", "MapReduce is a programming model and an associated implementation for processing and generating big data sets with a parallel distributed algorithm on a cluster A MapReduce program is composed of a map procedure or method which performs filtering and sorting such as sorting students by first name into queues one queue for each name")
  master ! InputPair("example3", "The model is a specialization of the split-apply-combine strategy for data analysis It is inspired by the map and reduce functions commonly used in functional programming although their purpose in the MapReduce framework is not the same as in their original forms")

  master ! Flush

}
