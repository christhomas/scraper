import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import java.net.URL

case class Start(url: URL)
case class ScrapeRequest(url: URL)
case class ScrapeResponse(url:URL,list: List[URL])
case class PageContent(url:URL,content:List[String],links:List[URL])


//Parent Actor for orchestrating the crawl and search.
class Handler (system:ActorSystem) extends Actor {

  def receive: Receive = {
    case Start(starturl) => scrapecontent(starturl)
    case ScrapeResponse(starturl, links) => println(s"$starturl was scraped")

      for (url <- links) {

        if (url.getHost == starturl.getHost) {

          println(s"scanning next page $url")
        }
        else {
          println(s"ignoring $url")
        }
     }
  }
  def scrapecontent(url: URL): Unit = {
    val scraper = system.actorOf(Props (new Scraper(self)))
    scraper ! ScrapeRequest(url)
  }
}

object Main extends App {

  val system = ActorSystem()

  val handler = system.actorOf(Props(new Handler(system)))

  val url = new URL("https://www.nytimes.com/?WT.z_jog=1&hF=t&vS=undefined")

  //initiate actor system
  handler ! Start(url)
}