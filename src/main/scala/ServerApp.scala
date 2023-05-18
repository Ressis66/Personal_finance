import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.comcast.ip4s.{Host, Port}
import domain.User
import io.circe.literal.JsonStringContext
import io.circe.{Decoder, Encoder}
import mongo4cats.bson.ObjectId
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRequest, AuthedRoutes, EntityDecoder, EntityEncoder, HttpRoutes}
import org.typelevel.ci.CIStringSyntax
import repositories.UserRepo

import java.time.LocalDateTime



object Server {

  implicit val usd : Decoder[User] = Decoder.instance{
    cur => for{
      name <-cur.downField("name").as[String]

    } yield User(new ObjectId, LocalDateTime.now(), LocalDateTime.now(), name)
  }
  implicit val userEncoder: EntityEncoder[IO, Option[User]]  = jsonEncoderOf[Option[User]]
  implicit val userDecoder: EntityDecoder[IO, User] = jsonOf[IO, User]
  implicit val us: Encoder[Option[User]] = Encoder.instance { (user: Option[User]) =>
    json"""{"hello": ${user.get.name}}"""
  }

  import cats.effect.unsafe.IORuntime
   implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
     val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of { case  GET -> Root / "user" / id =>
      Ok(UserRepo.apply.findUser(id))
    }

  def loginService : HttpRoutes[IO] = HttpRoutes.of {
    case r @ POST -> Root/"echo"=>{
      for{
        u <- r.as[User]
        _ <- UserRepo.apply.createUser(u)
        resp <- Ok(Option(u))
      } yield resp
    }
  }

      def sessionAuth: AuthMiddleware[IO, User] =
        authedRoutes =>
          Kleisli { req =>
            req.headers.get(ci"X-User-ID") match {
              case Some(nel) =>
                val id = nel.head.value
                for {
                  users <- OptionT.liftF(UserRepo.apply.findUser(id))
                  result <-
                    if (users.contains(id))
                      authedRoutes(AuthedRequest(UserRepo.apply.findUser(id), req))
                    else OptionT.liftF(Forbidden("You shall not pass"))
                } yield result
              case None => OptionT.liftF(Forbidden("No header"))
            }
          }

  val authedRoutes: AuthedRoutes[User, IO] =
    AuthedRoutes.of {
      case GET -> Root / "welcome" as user => Ok(s"Welcome, ${user.name}")
    }

  val service: HttpRoutes[IO] = sessionAuth(authedRoutes)

      val router = Router("/" -> service).orNotFound

      val server = for {
        s <- EmberServerBuilder
          .default[IO]
          .withPort(Port.fromInt(8080).get)
          .withHost(Host.fromString("localhost").get)
          .withHttpApp(router)
          .build
      } yield s


}