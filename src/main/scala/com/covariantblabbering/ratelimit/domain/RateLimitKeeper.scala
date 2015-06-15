package com.covariantblabbering.ratelimit.domain


import scalaz.{Monoid, Traverse, Coyoneda, Free}

/**
 * Created by guillermo on 8/06/15.
 */

//ref: http://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html

trait RateLimitPolicy
trait RateLimitBucket

object RateLimitBucket{
  def from(policy: RateLimitPolicy): RateLimitBucket = ???
}

trait RateLimitState{
  def policies: List[RateLimitPolicy]
}

class RateLimitTransition

trait RateLimitOp[A]

object Cache{

  case class GetBucket(policy: RateLimitPolicy) extends RateLimitOp[Option[RateLimitBucket]]
  case class UpdatePolicy(bucket: Option[RateLimitBucket], state: RateLimitState) extends RateLimitOp[RateLimitTransition]

  case class Watch(key: String) extends RateLimitOp[Boolean]
  case class Fetch(key: String) extends RateLimitOp[Option[RateLimitState]]
  case class Transit(state:Option[RateLimitState], f: RateLimitState => Free[Cacheable, List[RateLimitTransition]]) extends RateLimitOp[List[RateLimitTransition]]
  case class Persist(transition: List[RateLimitTransition]) extends RateLimitOp[(Boolean,RateLimitState)]

  sealed trait Cache[A]
  final case class Pure[A](a:A) extends Cache[A]
  final case class Perform[A](op: RateLimitOp[A]) extends Cache[A]

  //Coyoneda turns Cache[A] into Coyoneda[Cache,A] and Coyoneda is a functor, which we need for the Free monad
  //http://stackoverflow.com/questions/25403944/using-free-with-a-non-functor-in-scalaz
  type Cacheable[A] = Coyoneda[Cache,A]

  def pure[A](a: A): Free[Cacheable,A] = Free.liftFC(Pure(a)) // Handy lifting using coyoneda
  def perform[A](op: RateLimitOp[A]): Free[Cacheable,A] = Free.liftFC(Perform(op))
}

class DefaultRateLimitKeeper{

  import Cache._

  def getAndSet(key: String)(f: RateLimitState => Free[Cacheable, List[RateLimitTransition]]): Free[Cacheable,(Boolean,RateLimitState)] = {

    for{
      _ <- perform(Watch(key))
      st <- perform(Fetch(key))
      tr <- perform(Transit(st,f))
      e <- perform(Persist(tr))
    } yield e

  }

}

class RateLimitProvider(keeper: DefaultRateLimitKeeper)
{

  import Cache._

  def update(key: String): Free[Cacheable,(Boolean, RateLimitState)] = {

    val outcome = keeper.getAndSet(key)(rate => {

      rate.policies.map(policy => {

        for {
          bucket <- perform(GetBucket(policy))
          transition <- perform(UpdatePolicy(bucket, rate))
        } yield transition

      }).foldLeft(pure(List.empty[RateLimitTransition])) { (transitions, t) =>
        //Quick and dirty 'sequence' definition
        transitions flatMap (ll => t map (tt => tt +: ll))
      }
    })

    //TODO Need to check how to do a while using Free monads
/*    for{
      (updated,newState) <- outcome
      state <- if(updated) newState else update(key)
    } yield (state)*/

    outcome

  }

}
