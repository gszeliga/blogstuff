import com.covariantblabbering.builder.ApplicativeStyleWithMultipleMessages.SmartBuilderOps._
case class Person(val name:String, val lastname: String, val kk: String, val age:Int)
val ps = (Person
  @> "Hola".success
  @> "Que".success
  @> "Eps".success
  @> 1.success) toEither

val pf = (Person
  @> "UPS!".failure
  @> "Que".success
  @> "Eps".failure
  @> 1.success) toEither


//Making the Builder pattern as hipster as it can get