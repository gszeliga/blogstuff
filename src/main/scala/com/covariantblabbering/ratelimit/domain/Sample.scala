package com.covariantblabbering.ratelimit.domain

/**
 * Created by guillermo on 3/07/15.
 */
import scalaz.Free
import scalaz.State
import scalaz.~>


sealed trait ForthOperators[A]

final case class Push[A](value: Int, o: A) extends ForthOperators[A]
final case class Add[A](o: A) extends ForthOperators[A]
final case class Mul[A](o: A) extends ForthOperators[A]
final case class Dup[A](o: A) extends ForthOperators[A]
final case class End[A](o: A) extends ForthOperators[A]


object Forth {
  type ForthProg[A] = Free.FreeC[ForthOperators, A]

  import scala.language.implicitConversions
  implicit def liftForth[A](forth: ForthOperators[A]): ForthProg[A] = Free.liftFC(forth)

  def push(value: Int)  = Push(value, ())
  def add = Add(())
  def mul = Mul(())
  def dup = Dup(())
  def end = End(())
}



object Transforms {
  type Stack = List[Int]
  type StackState[A] = State[Stack, A]

  def runProgram: ForthOperators ~> StackState = new (ForthOperators ~> StackState) {
    def apply[A](t: ForthOperators[A]) : StackState[A] = t match {
      case Push(value : Int, cont) =>
        State((a: Stack) => (value::a, cont))
      case Add(cont) =>
        State((stack : Stack) => {
          val a :: b :: tail = stack
          ((a + b) :: tail, cont)
        })
      case Mul(cont) =>
        State((stack : Stack) => {
          val a :: b :: tail = stack
          ((a * b) :: tail, cont)
        })
      case Dup(cont) =>
        State((stack : Stack) => {
          val a :: tail = stack
          (a :: a :: tail, cont)
        })
      case End(cont) =>
        // This doesn't work as intended there may not
        // be a way to do this using ~>
        State((a : Stack) => (a, cont))
    }
  }

  import scalaz.Id._

  def printProgram: ForthOperators ~> Id = new (ForthOperators ~> Id) {
    def apply[A](t: ForthOperators[A]): Id[A] = t match {
      case Push(value: Int, cont) =>
        println(s"Push $value")
        cont
      case Add(cont) =>
        println("Add")
        cont
      case Mul(cont) =>
        println("Mul")
        cont
      case Dup(cont) =>
        println("Dup")
        cont
      case End(cont) =>
        println("End")
        cont
    }
  }




}

object test  {
  def main(args: Array[String])  = {
    import Forth._

    val square = for {
      _ <- dup
      _ <- mul
    } yield ()

    val testProg = for {
      _ <- push(3)
      _ <- square
      _ <- push(4)
      _ <- square
      _ <- add
    } yield ()


    val newTest = for {
      _ <- push(5)
      _ <- push(6)
    } yield ()


    //
    import Transforms.printProgram
    Free.runFC(testProg)(printProgram)



    // Using Natural Transformations
    import Transforms.runProgram
    println(Free.runFC(testProg)(runProgram).exec(List[Int]()))

  }
}
