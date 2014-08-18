package com.covariantblabbering.circuitbreaker.custom

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class Configuration(val name: String, val timeout: Long = 1000, val failureThreshold: Int = 3)
class CircuitBreakerStillOpenException(msg: String) extends Exception(msg)

class CircuitBreaker(private val conf: Configuration) {

  private object HalfOpen extends State {
    def postInvoke(cb: CircuitBreaker) = cb.reset

    def onError(cb: CircuitBreaker, source: Exception) = {
      cb.incrementAndGetFailureCount
      cb.tripFromState(this)
    }

    override def toString = "Half-Open"

  }

  private object Open extends State {

    def preInvoke(cb: CircuitBreaker) = {
      val now = System.currentTimeMillis;
      val elapsed = now - cb.tripTime;

      if (elapsed > cb.timeout) {
        cb.attemptReset;
      } else {
        //TODO Remove side effect
        throw new CircuitBreakerStillOpenException("Circuit breaker is still open");
      }
    }

    def enter(cb: CircuitBreaker) = cb.tripTime(System.currentTimeMillis)

    override def toString = "Open"
  }

  private object Close extends State {
    def postInvoke(cb: CircuitBreaker) = cb.resetFailureCount

    def onError(cb: CircuitBreaker, source: Exception) = {
      val failures = cb.incrementAndGetFailureCount
      val threshold = cb.failureThreshold

      if (failures >= threshold) cb.tripFromState(this)
    }

    def enter(cb: CircuitBreaker) = cb.resetFailureCount

    override def toString = "Close"

  }

  private val _state = new AtomicReference[State]
  private val _failureCount = new AtomicInteger(0)
  private val _tripTime = new AtomicLong(0)

  //Initialization
  transition(null, Close)

  private def transition(from: State, to: State) = {
    if (swap(from, to)) {
      to.enter(this)
    } else throw new IllegalStateException(s"Illegal transition attempted from ${from} to ${to}"); //TODO Remove side effect
  }

  private def swap(from: State, to: State) = _state.compareAndSet(from, to)

  def reset = transition(HalfOpen, Close)
  def tripTime(time: Long) = _tripTime.set(time)
  def tripTime = _tripTime.get
  def timeout = conf.timeout
  def attemptReset = transition(Open, HalfOpen)
  def failureThreshold = conf.failureThreshold

  def invoke[T](body: => T): Either[T, Exception] = {

    try {
      _state.get.preInvoke(this)
      val result = body
      _state.get.postInvoke(this)

      Left(result)
    } catch {
      case e: CircuitBreakerStillOpenException => Right(e)
      case e: IllegalStateException => Right(e)
      case e: Exception => {
        _state.get.onError(this, e)
        Right(e)
      }
    }
  }

  private[custom] def incrementAndGetFailureCount = _failureCount.incrementAndGet
  private[custom] def tripFromState(from: State) = transition(from, Open)
  private[custom] def resetFailureCount = _failureCount.set(0)
}

trait State {
  def preInvoke(cb: CircuitBreaker)
  def postInvoke(cb: CircuitBreaker)
  def onError(cb: CircuitBreaker, source: Exception)
  def enter(cb: CircuitBreaker)
}

object CircuitBreaker {

}