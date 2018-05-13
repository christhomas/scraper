import scala.concurrent._
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives

object WebServer extends Directives {
    implicit val system = ActorSystem("ExampleServer")

    import system.dispatcher

    implicit val materializer = ActorMaterializer()

    // settings about bind host/port
    // could be read from application.conf (via system.settings):
    val interface = "0.0.0.0"
    val port = 8080

    // A route is how we route requests into handling them
    val appRoutes =
        pathPrefix("search") {
            concat(
                path(Segment) { term =>
                  concat(
                      get {
                          complete(s"Search Term: $term");
                      }
                  )
                }
            )
        }
        get {
            path("") {
                complete("Welcome to Akka Search Engine")
            } ~
            path("ping") {
                complete("PONG!")
            } ~
            path("crash") {
                complete(sys.error("BOOM!"))
            }
        } ~
        pathPrefix("inner")(getFromResourceDirectory("someDir"))

    def main(args: Array[String]): Unit = {
        // Start the Akka HTTP server!
        // Using the mixed-in testRoutes (we could mix in more routes here)
        val bindingFuture = Http().bindAndHandle(appRoutes, interface, port)

        // Wait until user happy with the test-run and shut down the server afterwards.
        println(s"Server online at http://$interface:$port/")

        Await.result(system.whenTerminated, Duration.Inf)
    }
}

// Server definition
/*
object Main extends App {
    val system = ActorSystem()

  val handler = system.actorOf(Props(new Handler(system)))

  val url = new URL("https://www.nytimes.com/?WT.z_jog=1&hF=t&vS=undefined")

  //initiate actor system
  handler ! StartScraping(url)

  handler ! "searchprompt"
}*/
