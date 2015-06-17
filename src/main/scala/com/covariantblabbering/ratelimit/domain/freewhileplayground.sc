import com.covariantblabbering.ratelimit.domain.FreeWhile.doWhile

import scalaz.{Id, ~>, Free, Coyoneda}

object Ops{
  sealed trait Ops[A]
  final case class Value[A](a:A) extends Ops[A]
  final case class Add[A](v1: Ops[A], v2: Ops[A]) extends Ops[A]

  type MyOps[A] = Coyoneda[Ops,A]

  def value[A](v:A): Free[MyOps,A] = Free.liftFC(Value(v))

  val zero = value(0)
}

import Ops._

object MyInterpreter extends (Ops ~> Id.Id) {

  import Id._

  def apply[A](fa: Ops[A]): Id[A] = fa match {
    case Value(v) => v
    case Add(Value(v1),Value(v2)) => Value(1+2)
  }
}


def count(i: Int):Free[MyOps,Int] = {

  def doCount(partial:Int):Free[MyOps,Int] =
    doWhile[MyOps,Int](counter => counter < i) {
      () => value(partial) flatMap (v => doCount(v + 1))
    }

  doCount(0)

}

val counter = count(2)

Free.runFC(counter)(MyInterpreter)

