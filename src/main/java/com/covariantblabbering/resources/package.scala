package com.covariantblabbering

package object resources {
  
  type closable = { def close() }
  type releaseable = { def release() }

  trait Resource[R] {
    def open(r :R): Unit = ()
    def close(r: R)
  }

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
  
  implicit def closableResourceManager[A <: closable] = new Resource[A] {
    def close(r: A) = {println("executiong close() method...."); r.close}
  }

  implicit def releseableResourceManager[A <: releaseable] = new Resource[A] {
    def close(r: A) = {println("executing release() method...."); r.release}
  }  
  
//  implicit def fileInputStreamResource:Resource[java.io.FileInputStream] = new Resource[java.io.FileInputStream] {
//    def close(r: java.io.FileInputStream) = {println("explicit file input stream...."); r.close}
//  }  
  
  def using[T](rs: T)(loanTo: T => Unit)(implicit r: Resource[T]): Outcome[T] = {
    try {
      
      r.open(rs)
      loanTo(rs)

      return Succeded
    }
    catch{
      case e:Exception => new Failure[T]{
        type E = e.type
        def source = rs
        def exception = e
      }
    } finally {
      r.close(rs)
    }
  }
}