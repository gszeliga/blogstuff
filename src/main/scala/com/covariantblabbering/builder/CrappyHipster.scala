package com.covariantblabbering.builder

case class CrappyHipster(facialHair: String, shirt: String, band: String, hobbie: String)

object CrappyHipsterRules {

  import com.covariantblabbering.builder.ApplicativeStyleWithExceptions._

  def facialHairStyle(hair: String) = {
    if(hair.toLowerCase.contains("beard")) Continue(hair)
    else Failure(new Exception("You need to make vikings envious with any bushy or scraggly beard"))
  }

  def tshirt(style: String) = {
    if(style.toLowerCase.contains("v-neck")) Continue(style)
    else Failure(new Exception("C'mon! We need to see some traces of your belly button"))
  }

  def acceptedBands(band: String) = {
    val acceptedBands = List("fleet foxes", "sun kil moon", "neutral milk hotel")
    acceptedBands find(_ == band.toLowerCase) map(Continue(_)) getOrElse Failure(new Exception(s"Do you still listen to '$band'? That name is not even poetic and/or ironic"))
  }

  def acceptedHobbies(hobbie: String) = {

    val acceptedHobbies = List("knitting", "urban beekeepin", "taxidermy")
    acceptedHobbies find(_ == hobbie.toLowerCase) map(Continue(_)) getOrElse Failure(new Exception(s"Nobody does '$hobbie' anymore today. Don't you got any friends around?"))

  }

}
