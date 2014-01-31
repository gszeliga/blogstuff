package com.covariantblabbering.phantom2

import java.util.Date

trait Translation
trait MissingTranslation
trait Trans extends Translation with MissingTranslation

trait Certificate
trait MissingCertificate
trait Cert extends Certificate with MissingCertificate

trait Passport
trait MissingPassport
trait Pass extends Passport with MissingPassport

object VisaRequest {

  def apply(to: Someone, at: String) = new VisaRequest[MissingTranslation with MissingCertificate with MissingPassport](to, at)

  class HasTranslation[FROM, TO]
  object HasTranslation {
    implicit def hasTranslation[FROM >: Cert with Pass]: HasTranslation[FROM with MissingTranslation, FROM with Translation] = null
  }

  class HasCertificate[FROM, TO]
  object HasCertificate {
    implicit def hasCertificate[FROM >: Trans with Pass]: HasCertificate[FROM with MissingCertificate, FROM with Certificate] = null
  }

  class HasPassport[FROM, TO]
  object HasPassport {
    implicit def hasPassport[FROM >: Trans with Cert]: HasPassport[FROM with MissingPassport, FROM with Passport] = null
  }

  class VisaRequest[STATE](val to: Someone, val at: String) {
    def broughtMarriageTranslation[TO](implicit o: HasTranslation[STATE, TO]) = new VisaRequest[TO](to, at)
    def hasUpdatedCensus[TO](implicit o: HasCertificate[STATE, TO]) = new VisaRequest[TO](to, at)
    def validPassport[TO](implicit o: HasPassport[STATE, TO]) = new VisaRequest[TO](to, at)
  }

  class VisaIssuer(private val vr: VisaRequest[Translation with Certificate with Passport]) {
    def issue = new Visa(vr.to, new Date(), vr.at)
  }

  implicit def toIssuer(vr: VisaRequest[Certificate with Translation with Passport]) = {
    new VisaIssuer(vr)
  }

  class Visa(val to: Someone, val expiresAt: Date, val issuedAt: String) {
    override def toString = {
      s"subject: $to\nexpires_at: $expiresAt\nissued_at: $issuedAt"
    }
  }

}

class Someone(val name: String, val lastname: String) {
  override def toString = s"$lastname, $name"
}