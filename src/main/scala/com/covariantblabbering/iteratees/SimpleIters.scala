package com.covariantblabbering.iteratees

object SimpleIters {

  def length[E]: IterV[E, Int] = {
    def length(acc: Int): IterV[E, Int] = Cont[E, Int] {
      s: StreamG[E] =>
        s match {
          case Element(_) => length(acc + 1)
          case EMPTY => length(acc)
          case EOF => Done(acc, EOF)
        }
    }
    length(0)
  }

  def sum[E <: Int]: IterV[E, Int] = {

    def sum(acc: Int): IterV[E, Int] = Cont[E, Int] {
      s: StreamG[E] =>
        s match {
          case Element(value) => sum(acc + value)
          case EMPTY => sum(acc)
          case EOF => Done(acc, EOF)
        }
    }
    
    sum(0)
  }

}