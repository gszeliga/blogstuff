package com.covariantblabbering.iteratees

object test {

  //http://patterngazer.blogspot.co.uk/2012/03/where-we-explore-basics-of-iteratees.html

  RunIter.run(IterEnum.enum[Int, Int](List(1, 2, 3, 4), SimpleIters.length))
                                                  //> res0: Option[Int] = Some(4)

  RunIter.run(IterEnum.enum[Int, Int](List(1, 2, 3, 4), SimpleIters.sum))
                                                  //> res1: Option[Int] = Some(10)

  RunIter.run(IterEnum.enum[Int, Unit](List(1, 2, 3, 4), SimpleIters.drop(2)))
                                                  //> res2: Option[Unit] = Some(())

  RunIter.run(IterEnum.enum[Int, Option[Int]](List(8, 1, 2, 3, 4), SimpleIters.head))
                                                  //> res3: Option[Option[Int]] = Some(Some(8))

  RunIter.run(IterEnum.enum[Int, Option[Int]](List(8, 1, 2, 3, 4), SimpleIters.drop1Keep1))
                                                  //> res4: Option[Option[Int]] = Some(Some(1))
 
}