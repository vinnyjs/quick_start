import cats.conversions.all.autoWidenFunctor
import smithy4s.pet._
import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s._
import com.comcast.ip4s._
import smithy4s.http4s.SimpleRestJsonBuilder



object PetImpl extends PetService[IO] {
  val petList: List[Pet] = List();
  def getAllPets(): Option[List[Pet]] = {
    Option(petList).filter(_.nonEmpty)
  }
  def getAll(): IO[smithy4s.pet.AllPets] = {
    IO.pure {
      AllPets(getAllPets())
    }
  }
  
  def getPet(id: String): IO[smithy4s.pet.Result] = IO.pure {
    val name = Option(petList.filter(_.id.equals(id))).filter(_.nonEmpty)
    Result(s"ID: $id, Name: $name")
  }
		
  def postPet(id: Option[String], name: Option[String]): IO[smithy4s.pet.Result] = IO.pure {
    petList.appendedAll(List(Pet(id.get, name)))
    Result(s"ID: $id, Name: $name")
  }

  def putPet(id: String, name: Option[String]): IO[Result] = IO.pure {
    Result(s"ID: $id, Name: $name")
  }
}

object Routes {
  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(PetImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](PetService)

  val all: Resource[IO, HttpRoutes[IO]] = example.map(_ <+> docs)
}

object Main extends IOApp.Simple {

  val run = Routes.all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHost(host"0.0.0.0")
        .withHttpApp(routes.orNotFound)
        .build
    }
    .use(_ => IO.never)

}
