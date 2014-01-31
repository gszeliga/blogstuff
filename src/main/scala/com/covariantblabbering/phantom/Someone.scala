package com.covariantblabbering.phantom

abstract class RICH
abstract class HANDSOME
abstract class AWESOME

object Bottle {

  class Genie[R, H, A](val name: String, val lastname: String) {

    def hitTheJackpot = new Genie[RICH, H, A](name, lastname)
    def deadlyChickMagnet = new Genie[R, HANDSOME, A](name, lastname)
    def awesomePossum = new Genie[R, H, AWESOME](name, lastname)

    def concede = new Somebody[R, H, A](name, lastname)
  }

  def open(who: Nobody) = new Genie[Nothing, Nothing, Nothing](who.name, who.lastname)

}

class Somebody[R, H, A](val name: String, val lastname: String)
class Nobody(name: String, lastname: String) extends Somebody[Nothing, Nothing, Nothing](name, lastname)
