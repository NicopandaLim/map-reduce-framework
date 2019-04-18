package client

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props, ActorRef}
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.io.Source
import java.net.URL
import scala.util.matching.Regex

import common._
import client._

import java.util.ArrayList
import java.util.StringTokenizer

class LinkCount extends Mapred{


  
  override def mapFunction (name:String, url: String, reduceActors: ActorRef) = {

    var Lines = Source.fromURL(url).getLines()

    val pageLinks = List("http://reed.cs.depaul.edu/lperkovic/",
                         "http://research.microsoft.com/en-us/um/people/lamport/pubs/pubs.html#time-clocks",
                         "http://research.microsoft.com/en-us/um/people/lamport/pubs/pubs.html#chandy",
                         "http://docs.scala-lang.org/",
                         "http://docs.scala-lang.org/tour/pattern-matching.html",
                         "http://doc.akka.io/docs/akka/current/",
                         "http://doc.akka.io/docs/akka/current/scala.html",
                         "https://d2l.depaul.edu/",
                         "http://akka.io/docs/",
                         "http://www.scala-sbt.org/",
                         "(http://ramcloud.stanford.edu/raft.pdf)",
                         "http://research.google.com/archive/mapreduce.html",
                         "http://docs.oracle.com/javase/10/docs/api/")

    for (line : String <- Lines) {
      if (line.contains("href=")) {
        var start = line.indexOf("href=\"")  // <a href="https://d2l.depaul.edu/d2l/home">D2L</a>
        var end = line.indexOf("\"", start+6)
        var hyperlink: String = ""
        if ((start >=0) && (end >= 0)) {hyperlink = line.substring(start+6, end)}
        if (pageLinks.contains(hyperlink)) {
          reduceActors ! Intermediate(hyperlink, "one")
        }
      }
    }
    
  }

  def redFunction(k: String, text: String, reduceMap : HashMap[String, List[String]]): HashMap[String, List[String]] = {
    if (reduceMap.contains(k)) {
        reduceMap += (k -> (text :: reduceMap(k)))
      }else{
        reduceMap += (k -> List(text))
      }
    return reduceMap 
  }
}


object LinkCount3 extends App {
  
  val linkCnt = new LinkCount()
  val system = ActorSystem("LinkCount3", ConfigFactory.load.getConfig("client"))  
  val master = system.actorOf(Props(classOf[MasterActor], linkCnt), name = "master")
  
  master ! InputPair("csc536", "http://reed.cs.depaul.edu/lperkovic/csc536/")
  master ! InputPair("syllabus", "http://reed.cs.depaul.edu/lperkovic/csc536/syllabus.html")
  master ! InputPair("hw1", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework1.html")
  master ! InputPair("hw2", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework2.html")
  master ! InputPair("hw3", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework3.html")
  master ! InputPair("hw4", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework4.html")
  master ! InputPair("hw5", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework5.html")
  master ! InputPair("hw6", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework6.html")
  master ! InputPair("hw7", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework7.html")
  master ! InputPair("hw8", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/Homework8.html")
  master ! InputPair("finalProject", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/FinalProject.html")
  master ! Flush

}
