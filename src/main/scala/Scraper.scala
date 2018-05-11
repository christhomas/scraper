import java.net.URL

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup
import org.apache.commons.validator.routines.UrlValidator
import scala.collection.JavaConverters._

case class PageContent(url:URL,content:List[String],links:List[URL])

class Scraper (indexer: ActorRef) extends Actor {
  val urlValidator = new UrlValidator()
  println("Scrape Initialised")
  def receive: Receive = {
    case ScrapeRequest(url) =>
      val pagecontent = scrapecontent(url)
      sender() ! ScrapeResponse(url,pagecontent.links)
  }

  def scrapecontent(url:URL): PageContent = {
    val link = url.toString
    val connect = Jsoup.connect(link).execute()
    val contentType:String = connect.contentType

    if (contentType.startsWith("text/html")) {
      val content = connect.parse()
      val pageContent = content.text().split("\\.").toList
      val links: List[URL] = content.getElementsByTag("a").asScala.map(e => e.attr("href")).filter(s => urlValidator.isValid(s)).map(link => new URL(link)).toList
      PageContent(url, pageContent, links)
    } else {
      PageContent(url,List(),List())}



  }


}
