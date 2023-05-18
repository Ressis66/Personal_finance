package ru.personal

import cats.effect.{IO, IOApp}

object MainApp extends IOApp.Simple {

  def run: IO[Unit] =
    ServerAp.server.use(_ => IO.never)

}
