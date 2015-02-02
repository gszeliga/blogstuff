

import com.covariantblabbering.builder.ApplicativeStyle.SmartBuilder._
import com.covariantblabbering.builder.ApplicativeStyle.Continue
case class Person(val name:String, val lastname: String, val kk: String, val age:Int)
Person ? Continue("Hola") ? Continue("Que") ? Continue("Eps") ? Continue(1)
