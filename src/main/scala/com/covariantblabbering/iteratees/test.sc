package com.covariantblabbering.iteratees

object test {

	//http://patterngazer.blogspot.co.uk/2012/03/where-we-explore-basics-of-iteratees.html

  RunIter.run(IterEnum.enum(SimpleIters.length, List(1, 2, 3, 4)))
                                                  //> res0: Option[Int] = Some(4)

  RunIter.run(IterEnum.enum(SimpleIters.sum, List(1, 2, 3, 4)))
                                                  //> res1: Option[Int] = Some(10)

}