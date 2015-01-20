package com.covariantblabbering.builder

/**
 * Created by guillermo on 18/01/15.
 */
class ApplicativeStyle {

  trait Functor[F[_]]{
    def map[A,B](fa: F[A])(f: A => B): F[B]
  }

  trait Applicative[F[_]] extends Functor[F]
  {
    def map2[A,B,C](fa: F[A], fb: F[B])(f: (A,B) => C): F[C] = {
      apply(map(fa)(a => f(a,_:B)))(fb)
    }

    def apply[A,B](a: F[A => B])(fa:F[A]):F[B] = {
      map2(a,fa)(_(_))
    }

    def map[A,B](fa: F[A])(f: A => B):F[B] = {
      map2(unit(f),fa)(_(_))
    }

    def unit[A](a: A):F[A]
  }

  trait BuildStep[+E, +A]

  case class Continue[T](v: T) extends BuildStep[Nothing, T]
  case class Failure[E](e: E) extends BuildStep[E, Nothing]

  def applicativeBuilder = new Applicative[({type f[x] = BuildStep[Throwable, x]})#f] {
    def unit[A](a: A) = Continue(a)
    override def map[A, B](step: BuildStep[Throwable, A])(f: A => B) = {
      step match {
        case Continue(v) => {
          try
          {
            Continue(f(v))
          }
          catch{
            case e:Throwable => Failure(e)
          }
        }
        case f @ _ => f
      }

    }
  }
}
