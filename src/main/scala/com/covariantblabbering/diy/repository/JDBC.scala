package com.covariantblabbering.diy.repository

import scala.util.Success
import scala.util.Try
import scala.util.Failure

abstract class JDBC[+T] {
  def get: T
  def map[A](f: T => A): JDBC[A]
  def flatMap[A](f: T => JDBC[A]): JDBC[A]
  def foreach[A](f: T => A)
  def andIfHalted(f: Function1[Throwable, Unit]): JDBC[T]
  def asTry: Try[T]
}

object JDBC {

  def trying[P, R](f: Function0[R]) = () => {
    try {
      Success(f())
    } catch {
      case e: Throwable => Failure(e)
    }
  }

  def apply[T](f: () => T): JDBC[T] =
    apply(trying(f)())

  def apply[T](obj: Try[T]): JDBC[T] = {
    obj match {
      case Success(o) => new Continue(o)
      case Failure(e) => new Halt(e)
    }
  }
}

sealed case class Continue[T](obj: T) extends JDBC[T] {

  def get = obj

  def foreach[U](f: T => U) = f(obj)
  def map[A](f: T => A): JDBC[A] = JDBC {
    try {
      Success(f(obj))
    } catch {
      case e: Throwable => Failure(e)
    }
  }
  def flatMap[A](f: T => JDBC[A]): JDBC[A] = f(obj)
  def andIfHalted(f: Function1[Throwable, Unit]): JDBC[T] = this
  def asTry = new Success(get)

}

sealed case class Halt[T](e: Throwable) extends JDBC[T] {
  def get = throw e
  def foreach[U](f: T => U) = ()
  def map[A](f: T => A): JDBC[A] = this.asInstanceOf[JDBC[A]]
  def flatMap[A](f: T => JDBC[A]): JDBC[A] = this.asInstanceOf[JDBC[A]]
  def andIfHalted(f: Function1[Throwable, Unit]): JDBC[T] = {
    f(e)
    this
  }
  def asTry = new Failure(e)
}