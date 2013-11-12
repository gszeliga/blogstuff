package com.covariantblabbering

package object resources {
  
  type closable = { def close() }
  type releaseable = { def release() }

  trait Resource[R] {
    def open(r :R): Unit = ()
    def close(r: R)
  }

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
  
  implicit def closableResourceManager[A <: closable] = new Resource[A] {
    def close(r: A) = {println("executiong close() method...."); r.close}
  }

  implicit def releseableResourceManager[A <: releaseable] = new Resource[A] {
    def close(r: A) = {println("executing release() method...."); r.release}
  }  
  
//  implicit def fileInputStreamResource:Resource[java.io.FileInputStream] = new Resource[java.io.FileInputStream] {
//    def close(r: java.io.FileInputStream) = {println("explicit file input stream...."); r.close}
//  }  
  
  def using[T](rs: T)(loanTo: T => Unit)(implicit r: Resource[T]): Outcome = {
    try {
      
      r.open(rs)
      loanTo(rs)

      return Succeded
    }
    catch{
      case e:Exception => new Failure{
        type E = e.type
        def exception = e
      }
    } finally {
      r.close(rs)
    }
  }
}