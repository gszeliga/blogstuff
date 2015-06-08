package com.covariantblabbering.ratelimit.domain

import scalaz.Free.{Suspend, Return}
import scalaz.{Coyoneda, Functor, Free}

/**
 * Created by guillermo on 8/06/15.
 */

//ref: http://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html

class RateLimitState
class RateLimitTransition
class RateLimitEvaluation

trait RateOp[A]
case class Watch(key: String) extends RateOp[Boolean]
case class Fetch(key: String) extends RateOp[Option[RateLimitState]]
case class Evaluate(state:Option[RateLimitState], f: RateLimitState => RateLimitTransition) extends RateOp[RateLimitTransition]
case class Persist(transition: RateLimitTransition) extends RateOp[RateLimitEvaluation]

sealed trait Cache[A]
final case class Pure[A](a:A) extends Cache[A]
final case class Perform[A](op: RateOp[A]) extends Cache[A]

object Cache{

  //Coyoneda turns Cache[A] into Coyoneda[Cache,A] and Coyoneda is a functor, which we need for the Free monad
  //http://stackoverflow.com/questions/25403944/using-free-with-a-non-functor-in-scalaz
  type Cacheable[A] = Coyoneda[Cache,A]

  def pure[A](a: A): Free[Cacheable,A] = Free.liftFC(Pure(a)) // Handy lifting using coyoneda
  def perform[A](op: RateOp[A]): Free[Cacheable,A] = Free.liftFC(Perform(op))
}

class RateLimitKeeper{

  import Cache._

  def getAndSet(key: String)(f: RateLimitState => RateLimitTransition): Free[Cacheable,RateLimitEvaluation] = {

    for{
      _ <- perform(Watch(key))
      st <- perform(Fetch(key))
      tr <- perform(Evaluate(st,f))
      e <- perform(Persist(tr))
    } yield e

  }

}