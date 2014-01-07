package com.covariantblabbering.iteratees


sealed trait StreamG[+E]


//Stream data types
final case class Element[E](data: E) extends StreamG[E]
case object EOF extends StreamG[Nothing]
case object EMPTY extends StreamG[Nothing]