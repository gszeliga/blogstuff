package com.covariantblabbering.freemonad

import scalaz.Functor

/**
 * Created by guillermo on 4/05/15.
 */
sealed trait Free[F[_],A]{

  //We force F to be a functor (map)
  def map[B](f: A => B)(implicit functor: Functor[F]): Free[F,B] = ???

  def flatMap[B](f: A => Free[F,B])(implicit functor: Functor[F]): Free[F,B] = {
    this match {
      case Return(v) => f(v)
      case Suspend(s) => new Suspend[F,B](functor.map(s)(_ flatMap(f)))
    }
  }

}

final case class Return[F[_],A](v:A) extends Free[F,A]
final case class Suspend[F[_],A](s: F[Free[F,A]]) extends Free[F,A]

object Free{
  def point[F[_],A](v:A) = new Return[F,A](v)
}