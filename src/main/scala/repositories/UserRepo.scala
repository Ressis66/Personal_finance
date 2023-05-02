package repositories
import cats.effect.{IO, Resource}
import domain.User
import io.circe.generic.auto._
import mongo4cats.circe._
import mongo4cats.client.MongoClient
import mongo4cats.collection.operations.{Filter, Update}

import java.time.Instant


trait UserRepo  {
  def findUser(id: String): IO[Option[User]]
  def createUser(user: User): IO[Unit]
  def updateUser(user: User): Unit
  def deleteUser(user: User): Unit
}

object UserRepo {

  def apply: UserRepo =
    new UserRepo {
      val mongoClient: Resource[IO, MongoClient[IO]] =
        MongoClient.fromConnectionString[IO]("mongodb://localhost:27017")


      override def findUser(id: String): IO[Option[User]] = {

        val userFilter = Filter.gte("_id", Instant.parse(id))
        mongoClient.use { client =>
          for {
            db <- client.getDatabase("testdb")
            users <- db.getCollectionWithCodec[User]("users")
            user <- users.find(userFilter).first
          } yield user
        }
       /* val personCodecProvider = Macros.createCodecProvider[User]()
        val codecRegistry: CodecRegistry = fromRegistries(fromProviders(personCodecProvider), DEFAULT_CODEC_REGISTRY)
        val userDoc = users.find(Document("_id" -> id)).head()
        val bsonDocument = BsonDocumentWrapper.asBsonDocument(userDoc, DEFAULT_CODEC_REGISTRY)
        val bsonReader = new BsonDocumentReader(bsonDocument)
        val decoderContext = DecoderContext.builder.build
        val codec = codecRegistry.get(classTag[User].runtimeClass)
        val user: Option[User] = Option(codec.decode(bsonReader, decoderContext).asInstanceOf[User])
        user*/
      }

      override def createUser(user: User): IO[Unit] = {

          mongoClient.use { client =>
          for {
            db <- client.getDatabase("testdb")
            users <- db.getCollectionWithCodec[User]("users")
            _ <- users.insertOne(user)
          } yield ()
        }

        /*val codecProvider = Macros.createCodecProvider[User]()
        val codecRegistry: CodecRegistry = fromRegistries(fromProviders(codecProvider), DEFAULT_CODEC_REGISTRY)
        val codec = Macros.createCodec[User](codecRegistry)
        val encoderContext = EncoderContext.builder.isEncodingCollectibleDocument(true).build()
        val doc = BsonDocument()
        val writr = new BsonDocumentWriter(doc) // need to call new since Java lib w/o companion object
        codec.encode(writr, user, encoderContext)
        findUser(user._id.toString)*/
        }

      override def updateUser(user: User): Unit = {
        val userFilter = Filter.idEq(user._id)

        val amountUpdate = Update
          .set("_id", user._id)
        .set("createdAt", user.createdAt)
        .set("updatedAt", user.updatedAt)
        .set("name", user.name)
          .currentTimestamp("updatedAt")

        mongoClient.use { client =>
          for {
            db <- client.getDatabase("testdb")
            users <- db.getCollectionWithCodec[User]("users")
            _ <- users.updateOne(userFilter, amountUpdate)
          } yield ()
        }

      }
        /*users.updateOne(equal("_id", BsonObjectId(user._id.toString)),
        combine(set("createdAt", user.createdAt.toString),
          set("updatedAt", user.updatedAt.toString),
          set("name", user.name),
          set("categories", user.categories.toString),
          set("budgets", user.budgets.toString)))*/

      override def deleteUser(user: User): Unit = {
        val userFilter = Filter.idEq(user._id)

        mongoClient.use { client =>
          for {
            db <- client.getDatabase("testdb")
            users <- db.getCollectionWithCodec[User]("users")
            _ <- users.deleteOne(userFilter)
          } yield ()
        }

      }/*users.deleteOne(equal("_id", user._id.toString))*/
    }
}