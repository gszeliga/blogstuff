package com.covariantblabbering

//http://preshing.com/20110920/the-python-with-statement-by-example/

import java.io.FileReader
package object structural_typing {

  type closable = { def close(): Unit }

  trait Outcome[+T] {
    type E
    def withFallback(f: (T, E) => Unit)
  }

  trait Failure[T] extends Outcome[T] {

    def source: T
    def exception: E

    def withFallback(f: (T, E) => Unit) = {
      f(source, exception)
    }
  }

  object Succeded extends Outcome[Nothing] {
    def withFallback(f: (Nothing, E) => Unit) = Unit
  }

  def tryUsing[T <: closable](resource: T)(loanTo: T => Unit): Outcome[T] = {
    println("loaning resource...");

    try {
      loanTo(resource)
      return Succeded
    } catch {
      case e: Exception => return new Failure[T] {
        type E = e.type
        def source = resource
        def exception = e
      }
    } finally {
      println("closing...");
      resource.close
    }
  }
}