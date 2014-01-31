package com.covariantblabbering.phantom
 
object test_visa {

  val vr = VisaRequest(new Someone("Guillermo","Szeliga"), "Shanghai")
                                                  //> vr  : com.covariantblabbering.phantom.VisaRequest.VisaRequest[Nothing,Nothin
                                                  //| g,Nothing] = com.covariantblabbering.phantom.VisaRequest$VisaRequest@78ce5b1
                                                  //| c
  
  
  val ch1 = vr.validPassport                      //> ch1  : com.covariantblabbering.phantom.VisaRequest.VisaRequest[Nothing,Nothi
                                                  //| ng,com.covariantblabbering.phantom.Passport] = com.covariantblabbering.phant
                                                  //| om.VisaRequest$VisaRequest@b23b25c
  val ch2 = ch1.broughtMarriageTranslation        //> ch2  : com.covariantblabbering.phantom.VisaRequest.VisaRequest[com.covariant
                                                  //| blabbering.phantom.Translation,Nothing,com.covariantblabbering.phantom.Passp
                                                  //| ort] = com.covariantblabbering.phantom.VisaRequest$VisaRequest@5492bbba
  val ch3 = ch2.hasUpdatedCensus                  //> ch3  : com.covariantblabbering.phantom.VisaRequest.VisaRequest[com.covariant
                                                  //| blabbering.phantom.Translation,com.covariantblabbering.phantom.Census,com.co
                                                  //| variantblabbering.phantom.Passport] = com.covariantblabbering.phantom.VisaRe
                                                  //| quest$VisaRequest@6d62dbb6
  
  ch3.issue                                       //> res0: com.covariantblabbering.phantom.VisaRequest.Visa = subject: Szeliga, G
                                                  //| uillermo
                                                  //| expires_at: Fri Jan 31 16:32:43 CST 2014
                                                  //| issued_at: Shanghai
  
                                                  
}