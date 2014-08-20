package com.covariantblabbering.circuitbreaker.custom

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class Configuration(val name: String, val timeout: Long = 2000, val failureThreshold: Int = 3)
class CircuitBreakerStillOpenException(msg: String) extends Exception(msg)

class CircuitBreaker(private val conf: Configuration) {

  private object HalfOpen extends State {
    override def postInvoke(cb: CircuitBreaker) = cb.reset

    override def onError(cb: CircuitBreaker, source: Exception) = {
      cb.incrementAndGetFailureCount
      cb.tripFromState(this)
    }

    override def toString = "Half-Open"

  }

  private object Open extends State {

    override def preInvoke(cb: CircuitBreaker) = {
      val now = System.currentTimeMillis;
      val elapsed = now - cb.tripTime;

      if (elapsed > cb.timeout) {
        cb.attemptReset;
      } else {
        throw new CircuitBreakerStillOpenException(s"Circuit breaker is still open. Elapsed time: $elapsed");
      }
    }

    override def enter(cb: CircuitBreaker) = cb.tripTime(System.currentTimeMillis)

    override def toString = "Open"
  }

  private object Close extends State {
    override def postInvoke(cb: CircuitBreaker) = cb.resetFailureCount

    override def onError(cb: CircuitBreaker, source: Exception) = {
      val failures = cb.incrementAndGetFailureCount
      val threshold = cb.failureThreshold

      if (failures >= threshold) cb.tripFromState(this)
    }

    override def enter(cb: CircuitBreaker) = cb.resetFailureCount

    override def toString = "Close"

  }

  private val state = new AtomicReference[State]
  private[custom] val failureCount = new AtomicInteger(0)
  protected val _tripTime = new AtomicLong(0)

  //Initialization
  transition(null, Close)

  private def transition(from: State, to: State) = {

    println(s"[${conf.name}] Transition [${from} => ${to}] requested [fc: ${failureCount.get}, tt: ${_tripTime.get}]");

    if (swap(from, to)) {
      to.enter(this)
    } else throw new IllegalStateException(s"Illegal transition attempted from ${from} to ${to}")
  }

  private def swap(from: State, to: State) = state.compareAndSet(from, to)

  def reset = {
    println(s"[${conf.name}] Reset");
    transition(HalfOpen, Close)
  }
  def tripTime(time: Long) = _tripTime.set(time)
  def tripTime = _tripTime.get
  def timeout = conf.timeout
  def attemptReset = {
    println(s"[${conf.name}] Attempting reset");
    transition(Open, HalfOpen)
  }
  def failureThreshold = conf.failureThreshold
  def isOpen = state.get == Open
  def isClose = state.get == Close
  def isHalfOpen = state.get == HalfOpen

  def invoke[T](body: => T): Either[T, Exception] = {

    try {
      state.get.preInvoke(this)
      val result = body
      state.get.postInvoke(this)

      Left(result)
    } catch {
      case e: CircuitBreakerStillOpenException => Right(e)
      case e: IllegalStateException => Right(e)
      case e: Exception => {
        state.get.onError(this, e)
        Right(e)
      }
    }
  }

  private[custom] def incrementAndGetFailureCount = failureCount.incrementAndGet
  private[custom] def tripFromState(from: State) = {
    println(s"[${conf.name}] Trip from state [${from}]");
    transition(from, Open)
  }
  private[custom] def resetFailureCount = failureCount.set(0)
}

trait State {
  def preInvoke(cb: CircuitBreaker) = {}
  def postInvoke(cb: CircuitBreaker) = {}
  def onError(cb: CircuitBreaker, source: Exception) = {}
  def enter(cb: CircuitBreaker) = {}
}

object CircuitBreaker {
  def apply(conf: Configuration) = new CircuitBreaker(conf)
}