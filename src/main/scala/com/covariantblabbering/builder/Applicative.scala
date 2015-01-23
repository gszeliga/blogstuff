package com.covariantblabbering.builder

/**
 * Created by guillermo on 18/01/15.
 */
object ApplicativeStyle {

  type Curryable = { def curried: _ => _ }

  trait Functor[F[_]]{
    def map[A,B](fa: F[A])(f: A => B): F[B]
  }

  trait Applicative[F[_]] extends Functor[F]
  {
    def map2[A,B,C](fa: F[A], fb: F[B])(f: (A,B) => C): F[C] = {
      apply(map(fa)(a => f(a,_:B)))(fb)
    }

    def apply[A,B](a: F[A => B])(fa:F[A]):F[B] = {
      map2(a,fa)((f,v) => f(v))
    }

    override def map[A,B](fa: F[A])(f: A => B):F[B] = {
      map2(unit(f),fa)((f,v) => f(v))
    }

    def unit[A](a: A):F[A]
  }

  class SmartBuilder[A,B](val f: A => B)
  {
      val applicative = applicativeBuilder;
      val build = applicative.apply(applicative.unit(f))(_)

      def read(step: BuildStep[Throwable,A]) = build(step)
  }


  object SmartBuilder
  {
    def apply[A,B](f: A => B) =
    {
      new SmartBuilder(f)
    }
  }

  implicit def smartify[A,B](target: Curryable) = SmartBuilder(target.curried)


  trait BuildStep[+E, +A]

  case class Continue[T](v: T) extends BuildStep[Nothing, T]
  case class Failure[E](e: E) extends BuildStep[E, Nothing]

  def applicativeBuilder = new Applicative[({type f[x] = BuildStep[Throwable, x]})#f] {
    def unit[A](a: A) = Continue(a)

    override def map2[A, B, C](fa: BuildStep[Throwable, A], fb: BuildStep[Throwable, B])(f: (A, B) => C) = {

      (fa, fb) match{
        case (Continue(a), Continue(b)) => {

          println("Enter map2")

          try
          {
            Continue(f(a,b))
          }
          catch
            {
              case e: Throwable => Failure(e)
            }

        }
        case (f1 @ Failure(_), f2 @ Failure(_)) => f1
        case (f @ Failure(e), _) => f
        case (_, f @ Failure(e)) => f
      }

    }
  }
}
