package com.covariantblabbering.rotatingmap

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.async

class RotatingMap[K,V](val numBuckets:Int , val expiredCallback:Option[OnExpiration[K,V]]) {

  assert(numBuckets > 2, "Number of buckets must be greater or equal to 2")

  private var buckets: List[Map[K, V]] = (1 to numBuckets).foldRight(List.empty[Map[K,V]])((_, l) => new HashMap[K,V] :: l)

  def rotate: Map[K,V] = {

    def removeLast(source: List[Map[K,V]]) = {

      @tailrec
      def doRemoveLast(tmp:List[Map[K,V]], tail: List[Map[K,V]]): (Map[K,V],List[Map[K,V]]) = {

        tail match  {
          case (h :: Nil) => (h,tmp.reverse)
          case (h :: t) => doRemoveLast(h :: tmp, t)
        }
      }

      doRemoveLast(Nil, source)

    }

    val (dead, remaining) = removeLast(buckets)

    //Add new bucket at head
    buckets = new HashMap[K,V] :: remaining

    //Notify expirations
    async {
      expiredCallback map {
        dead.foreach(_)
      }
    }

    //return dead
    dead

  }

}
