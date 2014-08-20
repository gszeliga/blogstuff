package com.covariantblabbering.circuitbreaker.custom

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.Right

@RunWith(classOf[JUnitRunner])
class TestCircuitBreaker extends FlatSpec with Matchers{

  behavior of "Circuit breaker"
  
  it must "be able to be instantiated" in {
    
    CircuitBreaker(new Configuration("my-test-instance"))
    
  }

  it must "invoke body on 'Close' state" in {
    
    val cb = CircuitBreaker(new Configuration("my-test-instance"))
    
    cb.invoke{1 + 1} should be(Left(2))
    
  }
  
  it must "notify failure on 'Close' state" in {
    val cb = CircuitBreaker(new Configuration("my-test-instance"))
    
    cb.invoke{throw new RuntimeException("I've failed")}.isRight should be(true)
    cb.failureCount.get should be(1) 
  }

  it must "trip to state 'Open' after 3 failures" in {
    val cb = CircuitBreaker(new Configuration("my-test-instance"))
    
    cb.invoke{throw new RuntimeException("I've failed1")}
    cb.invoke{throw new RuntimeException("I've failed2")}
    cb.invoke{throw new RuntimeException("I've failed3")}
    
    cb.failureCount.get should be(3)
    cb.isOpen should be(true) 
  }  
  
  it must "throw an Exception on 'Open' state" in {
    val cb = CircuitBreaker(new Configuration("my-test-instance"))
    
    cb.invoke{throw new RuntimeException("I've failed1")}
    cb.invoke{throw new RuntimeException("I've failed2")}
    cb.invoke{throw new RuntimeException("I've failed3")}
    
    cb.invoke{1+1}.isRight should be(true)
    cb.failureCount.get should be(3)
  }    
  
  it must "attemp invokation after threshold expires" in {
    val cb = CircuitBreaker(new Configuration("my-test-instance"))
    
    cb.invoke{throw new RuntimeException("I've failed1")}
    cb.invoke{throw new RuntimeException("I've failed2")}
    cb.invoke{throw new RuntimeException("I've failed3")}
    
    cb.invoke{1+1}.isRight should be(true)
    
    Thread.sleep(2000L)
    
    cb.invoke(1+1) should be(Left(2))
    cb.failureCount.get should be(0)
    cb.isClose should be(true)
    
  }    
  
}