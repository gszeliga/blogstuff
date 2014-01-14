package com.covariantblabbering.iteratees

object SimpleIters {

  def length[E]: IterV[E, Int] = {
    def length(acc: Int): Input[E] => IterV[E, Int] = {
      s: Input[E] =>
        s match {
          case Element(_) => Cont(length(acc + 1))
          case EMPTY => Cont(length(acc))
          case EOF => Done(acc, EOF)
        }
    }
    Cont(length(0))
  }

  def sum[E <: Int]: IterV[E, Int] = {

    def sum(acc: Int): Input[E] => IterV[E, Int] = {
      s: Input[E] =>
        s match {
          case Element(value) => Cont(sum(acc + value))
          case EMPTY => Cont(sum(acc))
          case EOF => Done(acc, EOF)
        }
    }

    Cont(sum(0))
  }

  def drop[E](size: Int): IterV[E, Unit] = {

    def step: Input[E] => IterV[E, Unit] = {
      case Element(value) => drop(size - 1)
      case EMPTY => Cont(step)
      case EOF => Done((), EOF)
    }

    //By using EMPTY we're saying that we want to remove the value from the input 
    if (size == 0) Done((), EMPTY) else Cont(step)
  }

  def head[E]: IterV[E, Option[E]] = {

    def step: Input[E] => IterV[E, Option[E]] = {

      input =>
        input match {
          case Element(value) => Done(Some(value), EMPTY)
          case EMPTY => Cont(step)
          case EOF => Done(None, EOF)
        }

    }

    Cont(step)

  }

  def drop1Keep1[E]: IterV[E, Option[E]] = {

    //drop(1) flatMap (_ => head)

    for {
      _ <- drop(1)
      x <- head
    } yield x

  }
}