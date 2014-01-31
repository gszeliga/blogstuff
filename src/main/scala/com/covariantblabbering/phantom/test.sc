package com.covariantblabbering.phanom

import com.covariantblabbering.phantom.Nobody
import com.covariantblabbering.phantom.Somebody
import com.covariantblabbering.phantom.Bottle
import com.covariantblabbering.phantom.{ AWESOME, RICH, HANDSOME }

object test {

  val someguy = new Nobody("name", "lastname")    //> someguy  : com.covariantblabbering.phantom.Nobody = com.covariantblabbering.
                                                  //| phantom.Nobody@78105738

  val b = Bottle.open(someguy)                    //> b  : com.covariantblabbering.phantom.Bottle.Genie[Nothing,Nothing,Nothing] =
                                                  //|  com.covariantblabbering.phantom.Bottle$Genie@5492bbba

  val c = b.hitTheJackpot.concede                 //> c  : com.covariantblabbering.phantom.Somebody[com.covariantblabbering.phanto
                                                  //| m.RICH,Nothing,Nothing] = com.covariantblabbering.phantom.Somebody@6d62dbb6
  val d = b.hitTheJackpot.deadlyChickMagnet.concede
                                                  //> d  : com.covariantblabbering.phantom.Somebody[com.covariantblabbering.phanto
                                                  //| m.RICH,com.covariantblabbering.phantom.HANDSOME,Nothing] = com.covariantblab
                                                  //| bering.phantom.Somebody@7546c1d4
  val e = b.hitTheJackpot.deadlyChickMagnet.awesomePossum.concede
                                                  //> e  : com.covariantblabbering.phantom.Somebody[com.covariantblabbering.phanto
                                                  //| m.RICH,com.covariantblabbering.phantom.HANDSOME,com.covariantblabbering.phan
                                                  //| tom.AWESOME] = com.covariantblabbering.phantom.Somebody@785f8172
  type CR7 = Somebody[RICH, HANDSOME, AWESOME]

  val somebodyAtLast: CR7 = e                     //> somebodyAtLast  : com.covariantblabbering.phanom.test.CR7 = com.covariantbla
                                                  //| bbering.phantom.Somebody@785f8172

}