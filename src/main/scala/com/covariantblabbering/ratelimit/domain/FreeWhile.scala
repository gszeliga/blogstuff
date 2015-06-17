package com.covariantblabbering.ratelimit.domain

import scalaz.Free
import scalaz.Free.{Return, gosub, Suspend}

/**
 * Created by guillermo on 17/06/15.
 */
object FreeWhile {

  def doWhile[F,A](f: A => Boolean)(g: () => Free[F,A]):Free[F,A] = {
    gosub(g)(v => if(f(v)) doWhile(f)(g) else Return(v))
  }
  
}
