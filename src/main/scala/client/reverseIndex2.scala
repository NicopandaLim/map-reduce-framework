package client

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props, ActorRef}
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.io.Source

import common._
import client._

  class ReverseIndex extends Mapred{
 
  override def mapFunction (title: String, url: String, reduceActors: ActorRef) = {  
    val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
    var content = ""
    var namesFound = HashSet[String]()
    
    // Get the content at the given URL and return it as a string
    try {
      content = Source.fromURL(url).mkString
    } catch {     // If failure, just return an empty string
      case e: Exception => content = ""
    }
    
    for (word <- content.split("[\\p{Punct}\\s]+")) {
      if ((!STOP_WORDS_LIST.contains(word)) && word(0).isUpper && !namesFound.contains(word)) {
        namesFound += word
        
        reduceActors ! Intermediate(word, title)   // send only one time for the same word in each book
        
      }
    }
  }

  override def redFunction (word: String, title: String, reduceMap : HashMap[String, List[String]]): HashMap[String, List[String]] = {
  
    if (reduceMap.contains(word)) {
        if (!reduceMap(word).contains(title))
          reduceMap += (word -> (title :: reduceMap(word)))
      }
      else
        reduceMap += (word -> List(title))

    return reduceMap  

  }

}


object reverseIndex2 extends App {

  val system = ActorSystem("ReverseIndex2", ConfigFactory.load.getConfig("client"))
  val revIndex = new ReverseIndex()
  val master = system.actorOf(Props(classOf[MasterActor], revIndex), name = "master")

  master ! InputPair("A Tale of Two Cities", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg98.txt")
  master ! InputPair("The Pickwick Papers", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg580.txt")
  master ! InputPair("A Child's History of England", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg699.txt")
  master ! InputPair("The Old Curiosity Shop", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg700.txt")
  master ! InputPair("Oliver Twist", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg730.txt")
  master ! InputPair("David Copperfield", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg766.txt")

  master ! Flush

}
