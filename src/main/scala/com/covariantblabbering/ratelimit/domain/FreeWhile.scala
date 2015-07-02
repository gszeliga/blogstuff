package com.covariantblabbering.ratelimit.domain

import com.covariantblabbering.ratelimit.domain.Transform.evaluator

import scalaz.Free.{FreeC, gosub}
import scalaz.Free
import scalaz.State
import scalaz.~>

/**
 * Created by guillermo on 17/06/15.
 */
object FreeWhile {

  def doWhile[F[_],A](f: A => Boolean)(g: () => Free[F,A])(r: () => Free[F,A]):Free[F,A] = {
    gosub(g)(v => if(f(v)) doWhile(f)(g)(r) else r())
  }

}

sealed trait Ops[A]
final case class Value[A](a:A) extends Ops[A]
final case class Sum[A](v1: Ops[A], v2: Ops[A]) extends Ops[A]

object Ops{

  /*type MyOps[A] = Coyoneda[Ops,A]*/
  type MyOps[A] = FreeC[Ops, A]

  import scala.language.implicitConversions
  implicit def liftOps[A](op: Ops[A]): MyOps[A] = Free.liftFC(op)

  // So now We have a Free monad for console:
  /*  type OpsMonad[A] = Free.FreeC[Ops, A]
    implicit val OpsMonad: Monad[OpsMonad] = Free.freeMonad[({type λ[α] = Coyoneda[Ops, α]})#λ]*/

  def value[A](v:A) = Value(v)
  val zero = value(0)
}


object Transform{
  import Ops._

  type Partial[V] = State[Int,V] //<Context, State>
  /*type PartialMonad[A] = Free.FreeC[Partial, A]
  implicit val MonadP: Monad[PartialMonad] = Free.freeMonad[({type λ[α] = Coyoneda[Partial, α]})#λ]*/



/*  case object Evaluator extends (Ops ~> Partial) {

    import Ops._

    def apply[Int](op: Ops[Int]):Partial[Int] = op match {
      case Value(v) => State(s => (v, ()))
      /*case Add(Value(v1), Value(v2)) =>  State((s:Int) => (v1+s+v2, ()))*/
    }
  }*/

  def evaluator: Ops ~> Partial = new (Ops ~> Partial) {

    def apply[A](op: Ops[A]):Partial[A] = op match {
      case Value(v) => State(s => (s+1,v))
      /*case Add(Value(v1), Value(v2)) =>  State((s:Int) => (v1+s+v2, ()))*/
    }
  }
}


object TestMe{
  def go() = {

    import Ops._

    val counter = Ops.liftOps(value(2))


    Free.runFC(counter)(evaluator).exec(9)

  }
}
