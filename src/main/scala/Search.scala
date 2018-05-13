import akka.actor.{ActorRef, ActorSystem,Actor}
import scala.io.StdIn.readLine

class Search(supervisor:ActorRef,index:ActorRef) extends Actor{

  var searchquery = readLine("what term are looking for?: ")

  val searchstring = searchquery.split(" ").toList

    self ! SearchRequest(searchstring)

  def receive:Receive = {
  case SearchRequest(string) => println(s"Searching for $string")
      index ! SearchRequest(string)
  }
}
