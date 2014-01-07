package com.covariantblabbering.iteratees

object RunIter {
  def run[E, A](iter: IterV[E, A]): Option[A] = {

    def iterRun(next: IterV[E, A]) = next match {
      case Done(a, _) => Some(a)
      case _ => None
    }

    iter match {
      case Done(value, _) => Some(value)
      case c @ Cont(_) => iterRun(c(EOF))
    }

  }
}