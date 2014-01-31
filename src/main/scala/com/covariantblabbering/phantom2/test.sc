package com.covariantblabbering.phantom2
 
object test {
	val a = VisaRequest(new Someone("",""),"")//> a  : com.covariantblabbering.phantom2.VisaRequest.VisaRequest[com.covariantb
                                                  //| labbering.phantom2.MissingTranslation with com.covariantblabbering.phantom2.
                                                  //| MissingCertificate with com.covariantblabbering.phantom2.MissingPassport] = 
                                                  //| com.covariantblabbering.phantom2.VisaRequest$VisaRequest@78ce5b1c
  val b = a.broughtMarriageTranslation            //> b  : com.covariantblabbering.phantom2.VisaRequest.VisaRequest[com.covariantb
                                                  //| labbering.phantom2.MissingCertificate with com.covariantblabbering.phantom2.
                                                  //| MissingPassport with com.covariantblabbering.phantom2.Translation] = com.cov
                                                  //| ariantblabbering.phantom2.VisaRequest$VisaRequest@5492bbba
  val c = b.hasUpdatedCensus                      //> c  : com.covariantblabbering.phantom2.VisaRequest.VisaRequest[com.covariantb
                                                  //| labbering.phantom2.Translation with com.covariantblabbering.phantom2.Missing
                                                  //| Passport with com.covariantblabbering.phantom2.Certificate] = com.covariantb
                                                  //| labbering.phantom2.VisaRequest$VisaRequest@7546c1d4
                                                  
  c.validPassport.issue                           //> res0: com.covariantblabbering.phantom2.VisaRequest.Visa = subject: , 
                                                  //| expires_at: Fri Jan 31 18:08:17 CST 2014
                                                  //| issued_at: 

}