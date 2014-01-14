package com.covariantblabbering.iteratees


sealed trait Input[+E]


//Input values
final case class Element[E](data: E) extends Input[E]
case object EOF extends Input[Nothing]
case object EMPTY extends Input[Nothing]