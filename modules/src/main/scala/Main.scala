import example._
import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s._
import com.comcast.ip4s._
import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.ember.client.EmberClientBuilder

object HelloWorldImpl extends HelloWorldService[IO] {
  def hello(name: String, town: Option[String]): IO[Greeting] = IO.pure {
    town match {
      case None    => Greeting(s"Hello $name!")
      case Some(t) => Greeting(s"Hello $name from $t!")
    }
  }
}

object Routes {
  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(HelloWorldImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](HelloWorldService)

  val all: Resource[IO, HttpRoutes[IO]] = example.map(_ <+> docs)
}

object ClientImpl extends IOApp.Simple {

  val helloWorldClient: Resource[IO, HelloWorldService[IO]] = for {
    client <- EmberClientBuilder.default[IO].build
    helloClient <- SimpleRestJsonBuilder(HelloWorldService).clientResource(
      client,
      Uri.unsafeFromString("http://localhost:9000")
    )
  } yield helloClient

  val run = helloWorldClient.use(c =>
    c.hello("Sam", Some("New York City"))
      .flatMap(greeting => IO.println(greeting.message))
  )

}

object Main extends IOApp.Simple {

  val run = Routes.all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHost(host"localhost")
        .withHttpApp(routes.orNotFound)
        .build
    }
    .use(_ => IO.never)

}