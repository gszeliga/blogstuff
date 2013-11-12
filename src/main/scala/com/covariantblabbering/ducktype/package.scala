package com.covariantblabbering

//http://preshing.com/20110920/the-python-with-statement-by-example/

import java.io.FileReader
package object structural_typing {

  type closable = { def close(): Unit }

  trait Outcome {
    type E <: Exception
    def withFallback(f: E => Unit)
  }

  trait Failure extends Outcome {

    def exception: E

    def withFallback(f: E => Unit) = {
      f(exception)
    }
  }

  object Succeded extends Outcome {
    def withFallback(f: E => Unit) = Unit
  }

  def tryUsing[T <: closable](resource: T)(loanTo: T => Unit): Outcome = {
    println("loaning resource...");

    try {
      loanTo(resource)
      return Succeded
    } catch {
      case e: Exception => return new Failure {
        type E = e.type
        def exception = e
      }
    } finally {
      println("closing...");
      resource.close
    }
  }
}