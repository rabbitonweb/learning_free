package free

import scalaz._
import Scalaz._

trait IO[A]
case class PrintLine(str: String) extends IO[Unit]
object GetLine extends IO[String]

object IO {
  def printLine(str: String): Free[IO, Unit] = Free.liftF(PrintLine(str))
  def getLine: Free[IO, String] = Free.liftF(GetLine)
}


class IOs[F[_]](implicit I : Inject[IO, F]) {

  def printLine(str: String): Free[F, Unit] = Free.liftF(I.inj(PrintLine(str)))
  def getLine: Free[F, String] = Free.liftF(I.inj(GetLine))

}

object IOConsoleInterpreter extends (IO ~> Id) {
  def apply[A](io: IO[A]): Id[A] = io match {
    case PrintLine(str) => println(str)
    case GetLine => scala.io.StdIn.readLine()
  }
}

object FreeIO extends App {

  import IO._

  val program: Free[IO, String] = for {
    _ <- printLine("Tell me your first name")
    first <- getLine
    _ <- printLine("Tell me your last name")
    last <- getLine
    _ <- printLine(s"Your name is $first $last")
  } yield first + " " + last

  program.foldMap(IOConsoleInterpreter)
}
