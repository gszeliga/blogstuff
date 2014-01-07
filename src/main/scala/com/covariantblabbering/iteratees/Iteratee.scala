package com.covariantblabbering.iteratees

sealed trait IterV[-E, +A]

//Offers a result
case class Done[E,A](value: A, stream: StreamG[E]) extends IterV[E,A]

//Offers to continue
//Check that f() evaluates the current stream and determines whether we reached to an end (Done) or not (Cont)
case class Cont[E,A](f: StreamG[E] => IterV[E,A]) extends IterV[E,A] {
  def apply[F >: E](s: StreamG[E]) = f(s)
}