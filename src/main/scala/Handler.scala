import akka.actor.{Actor, ActorPath, ActorSystem, PoisonPill, Props}
import scala.collection.mutable.ListBuffer
import java.net.URL

case class StartScraping(url: URL)
case class ScrapeRequest(url: URL)
case class IndexRequest(content: PageContent)
case class ScrapeResponse(url:URL,list: List[URL])
case class PageContent(url:URL,content:List[String],links:List[URL])
case class SearchRequest(search:List[String])

//Parent Actor for orchestrating the crawl and search.
class Handler (system:ActorSystem) extends Actor {

  val indexer = context actorOf Props(new Indexer(self))

  var wasscraped = Set.empty[URL]
  var indexstore = Map.empty[URL,List[String]]
  var actorslist = new ListBuffer[ActorPath]

  def receive: Receive = {
    case StartScraping(starturl) => scrapecontent(starturl)

    case ScrapeResponse(url, links) =>
        wasscraped += url

        //takes list of returned links and filters down to the links on the same host
        val toscrape = links.filter(_.getHost == url.getHost)

        toscrape.map(x => if (!wasscraped.contains(x)) scrapecontent(x))

        sender() ! PoisonPill

        val toremove = sender().path

        actorslist -= toremove

    case "searchprompt" => initsearchactor()
  }

  def scrapecontent(url: URL): Unit = {
    if (actorslist.size < 10) {
      val scraper = system.actorOf(Props (new Scraper(self,indexer)))

      actorslist += scraper.path

      scraper ! ScrapeRequest(url)
    }
  }

  def initsearchactor(): Unit = {
    val searchactor = context actorOf Props(new Search(self,indexer))
  }
}