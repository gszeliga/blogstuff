package com.covariantblabbering.builder


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




object ApplicativeStyleWithExceptions {

  trait BuildStep[+E, +A]

  case class Continue[T](v: T) extends BuildStep[Nothing, T]
  case class Failure[E](e: E) extends BuildStep[E, Nothing]

  type Curryable[-A,-B, +C] = { def curried: A => Function[B,C] }

  final class SmartBuilder[A,B](val f: BuildStep[Throwable,A => B])
  {
      def @> [E <: Throwable](step: BuildStep[E,A])(implicit applicative: Applicative[({type f[x] = BuildStep[Throwable, x]})#f]) = {
        applicative.apply(f)(step)
      }
  }

  object SmartBuilderOps
  {
    def <<= [A](f:  BuildStep[Throwable, A]) =  f match {
      case Continue(v) => v
      case Failure(e) => e
    }

    implicit val applicative = applicativeBuilder[Throwable]

    implicit def build[A,B](s: BuildStep[Throwable,A => B])=new SmartBuilder(s)
    implicit def smartify[A,B,C](target: Curryable[A,B,C])(implicit applicative: Applicative[({type f[x] = BuildStep[Throwable, x]})#f]) = build(applicative.unit(target.curried))

  }

  def applicativeBuilder[E] = new Applicative[({type f[x] = BuildStep[E, x]})#f] {
    def unit[A](a: A) = Continue(a)

    override def map2[A, B, C](fa: BuildStep[E, A], fb: BuildStep[E, B])(f: (A, B) => C) = {

      (fa, fb) match{
        case (Continue(a), Continue(b)) => Continue(f(a,b))
        case (Failure(e1), Failure(e2)) => Failure(e1)
        case (f @ Failure(_), _) => f
        case (_, f @ Failure(_)) => f
      }
    }
  }
}

object ApplicativeStyleWithMultipleMessages {

  sealed trait BuildStep[+E, +A]{
    def toEither: Either[E,A]
  }

  final case class Continue[A](v: A) extends BuildStep[Nothing,A] {
    def toEither = Right(v)
  }
  final case class Failure[F](e: List[F]) extends BuildStep[List[F], Nothing] {
    def toEither = Left(e)
  }

  type Curryable[-A,-B, +C] = { def curried: A => Function[B,C] }

  def applicativeBuilder[E] = new Applicative[({type f[x] = BuildStep[List[E], x]})#f] {
    def unit[A](a: A) = Continue(a)

    override def map2[A, B, C](fa: BuildStep[List[E], A], fb: BuildStep[List[E], B])(f: (A, B) => C) = {

      (fa, fb) match{
        case (Continue(a), Continue(b)) => Continue(f(a,b))
        case (Failure(l1), Failure(l2)) => Failure(l1 ++ l2)
        case (f @ Failure(_), _) => f
        case (_, f @ Failure(_)) => f
      }

    }
  }

  final class SmartBuilderOps[E,A,B](val f: BuildStep[E,A => B])
  {
    def @> (step: BuildStep[E,A])(implicit applicative: Applicative[({type f[x] = BuildStep[E, x]})#f]) = {
      applicative.apply(f)(step)
    }
  }

  object SmartBuilderOps
  {
    implicit val applicative = applicativeBuilder[String]

    implicit def toSmartBuilderOps[E,A,B](s: BuildStep[E,A => B])=new SmartBuilderOps(s)
    implicit def smartify[E,A,B,C](target: Curryable[A,B,C])(implicit applicative: Applicative[({type f[x] = BuildStep[E, x]})#f]) = toSmartBuilderOps(applicative.unit(target.curried))
    implicit def toValidationOps[T](v: T) = new ValidationOps(v)

  }

  class ValidationOps[T](v: T){
    def failure = Failure(List(v))
    def success = Continue(v)
  }
}
