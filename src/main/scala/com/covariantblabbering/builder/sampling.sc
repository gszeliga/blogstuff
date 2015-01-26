

import com.covariantblabbering.builder.{Animal, ApplicativeStyle}
import com.covariantblabbering.builder.ApplicativeStyle._
import com.covariantblabbering.builder.ApplicativeStyle.Continue
case class Person(val name:String, val lastname: String, val kk: String)
class Animal(val name:String, val lastname: String)
val applicative = ApplicativeStyle.applicativeBuilder
/*
val builder = applicative.unit(Person.curried)
val step1 = applicative.apply(builder)(_)
val step2 = applicative.apply(step1)(Continue("Szeliga"))*/

/*val a:Curryable[String, String => AnyRef] = Person

a.curried("Hola")("Hola")*/
smartify(Person) read Continue("Hola")
