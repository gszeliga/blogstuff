package com.covariantblabbering.diy.endpoint

import java.util.concurrent.BlockingQueue

trait Producer[T] {

  protected[this] val queue: BlockingQueue[T]

  final def put(event: T) = queue.put(event)
  final def offer(event: T) = queue.offer(event)
}

trait Consumer[T] extends Runnable {

  protected[this] val queue: BlockingQueue[T]

  final def run = {
    while (true) {
      try {
        onEvent(queue.take)
      } catch {
        case e: InterruptedException => Thread.currentThread.interrupt
      }
    }
  }

  def onEvent(event: T)
}