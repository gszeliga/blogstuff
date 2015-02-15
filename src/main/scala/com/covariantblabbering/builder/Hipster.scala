package com.covariantblabbering.builder

case class Hipster(facialHair: String, shirt: String, band: String, hobbie: String)

object HipsterRules {

  import com.covariantblabbering.builder.ApplicativeStyleWithMultipleMessages.SmartBuilderOps._

  def facialHairStyle(hair: String) = {
    if(hair.toLowerCase.contains("beard")) hair.success
    else "You need to make vikings envious with any bushy or scraggly beard".failure
  }

  def tshirt(style: String) = {
    if(style.toLowerCase.contains("v-neck")) style.success
    else "C'mon! We need to see some traces of your belly button".failure
  }

  def acceptedBands(band: String) = {
    val acceptedBands = List("fleet foxes", "sun kil moon", "neutral milk hotel")
    acceptedBands find(_ == band.toLowerCase) map(_.success) getOrElse s"Do you still listen to '$band'? That name is not even poetic and/or ironic".failure
  }

  def acceptedHobbies(hobbie: String) = {

    val acceptedHobbies = List("knitting", "urban beekeepin", "taxidermy")
    acceptedHobbies find(_ == hobbie.toLowerCase) map(_.success) getOrElse s"Nobody does '$hobbie' anymore today. Don't you got any friends around?".failure

  }

}
