

import com.covariantblabbering.builder.ApplicativeStyleWithExceptions.SmartBuilderOps._
import com.covariantblabbering.builder.ApplicativeStyleWithExceptions._
case class Person(val name:String, val lastname: String, val kk: String, val age:Int)
val ps = <<= (Person
              @> Continue("Hola")
              @> Continue("Que")
              @> Continue("Eps")
              @> Continue(1))

val pf = <<= (Person
              @> Failure(new Exception("UPS!"))
              @> Continue("Que")
              @> Continue("Eps")
              @> Continue(1))
