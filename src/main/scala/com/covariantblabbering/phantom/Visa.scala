package com.covariantblabbering.phantom

import java.util.Date

abstract class Census
abstract class Translation
abstract class Passport

object VisaRequest {

  def apply(to: Someone, at: String) = new VisaRequest(to, at)

  class VisaRequest[T, C, P](val to: Someone, val at: String) {
    def hasUpdatedCensus = new VisaRequest[T, Census, P](to, at)
    def broughtMarriageTranslation = new VisaRequest[Translation, C, P](to, at)
    def validPassport = new VisaRequest[T, C, Passport](to, at)
  }

  class VisaIssuer(private val vr: VisaRequest[Translation, Census, Passport]){
    def issue = new Visa(vr.to, new Date(), vr.at)
  }
  
  implicit def toIssuer(vr: VisaRequest[Translation, Census, Passport]) = {
    new VisaIssuer(vr)
  }
  
  class Visa(val to: Someone, val expiresAt: Date, val issuedAt: String){
    override def toString = {
      s"subject: $to\nexpires_at: $expiresAt\nissued_at: $issuedAt"
    }
  }
  
}

class Someone(val name: String, val lastname: String) {
  override def toString = s"$lastname, $name"
}