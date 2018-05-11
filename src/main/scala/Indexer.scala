import java.net.URL
import akka.actor.{Actor,ActorRef}

class Indexer (supervisor: ActorRef) extends Actor {

  var store = scala.collection.mutable.Map.empty[String, List[URL]]

  //receives scraping content and inverts it
  def receive: Receive = {

    case IndexRequest(content) =>
      val pageurl = content.url

      for (record <- content.content) {

        if (store contains (record)) {

          store(record) = pageurl :: store(record).distinct
        }
        else {
          store += (record -> List(pageurl).distinct)

        }
      }

    //makes a search on the inverted index store
    case SearchRequest(searchstring) =>

      val keywordget = (keyword: String) => {store.get(keyword) }

      val result = searchstring.map(keywordget)

      println(result)

      supervisor ! "searchprompt"

      sender() ! result


  }

}