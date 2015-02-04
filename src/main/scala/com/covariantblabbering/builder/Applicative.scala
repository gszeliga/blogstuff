package com.covariantblabbering.builder

/**
 * Created by guillermo on 18/01/15.
 */
object ApplicativeStyle {

  type Curryable[-A,-B, +C] = { def curried: A => Function[B,C] }

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

  final class SmartBuilder[A,B](val f: BuildStep[Throwable,A => B])
  {
      def @> [E <: Throwable](step: BuildStep[E,A])(implicit applicative: Applicative[({type f[x] = BuildStep[Throwable, x]})#f]) = {
        applicative.apply(f)(step)
      }
  }

  object SmartBuilderOps
  {
    def <<= [A](f:  BuildStep[Throwable, A]) =  f match {
      case Continue(v) =>v
      case Failure(e) => e
    }

    implicit val applicative = ApplicativeStyle.applicativeBuilder

    implicit def build[A,B](s: BuildStep[Throwable,A => B])=new SmartBuilder(s)
    implicit def step[A,B](f: A=> B)(implicit applicative: Applicative[({type f[x] = BuildStep[Throwable, x]})#f]) = applicative.unit(f)
    implicit def smartify[A,B,C](target: Curryable[A,B,C])(implicit applicative: Applicative[({type f[x] = BuildStep[Throwable, x]})#f]) = build(step(target.curried))

  }

  trait BuildStep[+E, +A]

  case class Continue[T](v: T) extends BuildStep[Nothing, T]
  case class Failure[E](e: E) extends BuildStep[E, Nothing]

  def applicativeBuilder = new Applicative[({type f[x] = BuildStep[Throwable, x]})#f] {
    def unit[A](a: A) = Continue(a)

    override def map2[A, B, C](fa: BuildStep[Throwable, A], fb: BuildStep[Throwable, B])(f: (A, B) => C) = {

      (fa, fb) match{
        case (Continue(a), Continue(b)) => {

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
