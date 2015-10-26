package com.covariantblabbering.rotatingmap

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

class RotatingMap[K, V](val numBuckets: Int, val expiredCallback: Option[OnExpiration[K, V]]) {

  assert(numBuckets > 2, "Number of buckets must be greater or equal to 2")

  private var _buckets: List[Map[K, V]] = (1 to numBuckets).foldRight(List.empty[Map[K, V]])((_, l) => new HashMap[K, V] :: l)

  def rotate: Map[K, V] = {

    def removeLast(source: List[Map[K, V]]) = {

      @tailrec
      def doRemoveLast(tmp: List[Map[K, V]], tail: List[Map[K, V]]): (Map[K, V], List[Map[K, V]]) = {

        tail match {
          case (h :: Nil) => (h, tmp.reverse)
          case (h :: t) => doRemoveLast(h :: tmp, t)
        }
      }

      doRemoveLast(Nil, source)

    }

    val (dead, remaining) = removeLast(_buckets)

    //Add new bucket at head
    _buckets = new HashMap[K, V] :: remaining

    //Notify expirations

    expiredCallback foreach {
      dead.foreach(_)
    }

    //return dead
    dead

  }

  def containsKey(key: K): Boolean = _buckets.exists(_.contains(key))

  def get(key: K): Option[V] = _buckets.find(_.contains(key)) flatMap (_.get(key))

  def put(key: K, value: V) = {
    val head = _buckets.head + (key -> value)
    val tail = _buckets.tail.foldLeft(List.empty[Map[K, V]]) { (l, m) => (m - key) :: l}

    _buckets = head :: tail
  }

  def remove(key: K): Option[V] = {
    val (removed, remaining) = _buckets.foldLeft((Option.empty[V], List.empty[Map[K, V]])) { (acc, m) =>

      acc match {
        case (r, l) => {
          if (m.contains(key)) {
            (Some(m(key)), m - key :: l)
          }
          else (r, m :: l)
        }
      }
    }

    _buckets = remaining

    removed
  }

  def size: Int = _buckets.foldLeft(0){ _ + _.size}

}
