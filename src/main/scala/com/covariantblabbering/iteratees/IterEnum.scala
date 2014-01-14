package com.covariantblabbering.iteratees

object IterEnum {

  //Traverses the whole sequence
  def enum[E,A](elements: Seq[E], iter: IterV[E,A]): IterV[E,A] = {
    (iter, elements) match {
      case _ if elements.isEmpty => iter
      case (Done(_,_), _) => iter
      case (c @ Cont(_), (e :: es)) => enum(es, c(Element(e)))
    }
  }
  
}