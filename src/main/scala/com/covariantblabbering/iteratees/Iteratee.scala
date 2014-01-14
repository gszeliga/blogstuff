package com.covariantblabbering.iteratees

sealed trait IterV[E, A] {

  def map[B](f: A => B): IterV[E, B] = {
    this match {
      case Done(value, input) => Done(f(value),input)
      case Cont(func) => Cont(e => func(e) map f)
    }
  }
  
  def flatMap[B](f: A => IterV[E, B]): IterV[E, B] = {
    this match {
      case Done(value, input) => f(value) match {
        case Done(otherValue, _) => Done(otherValue, input)
        case Cont(func) => func(input)
      }
      case Cont(func) => Cont(e => func(e) flatMap f)
    }

  }
}

//Offers a result
case class Done[E, A](value: A, input: Input[E]) extends IterV[E, A]

//Offers to continue
//Check that f() evaluates the current stream and determines whether we reached to an end (Done) or not (Cont)
case class Cont[E, A](f: Input[E] => IterV[E, A]) extends IterV[E, A] {
  def apply[F >: E](s: Input[E]) = f(s)
}