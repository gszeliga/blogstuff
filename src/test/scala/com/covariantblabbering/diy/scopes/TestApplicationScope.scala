package com.covariantblabbering.diy.scopes

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers

@RunWith(classOf[JUnitRunner])
class TestApplicationScope extends FlatSpec with Matchers{
	
  behavior of "Application scope"
  
  "Using default application.conf" must " create an instance of ApplicationScope" in {
    val scope = ApplicationScope()
    
    assert(scope != null) 
  }

  it must " load database information" in {
    val scope = ApplicationScope()
    		
    assert(scope.databaseConfiguration != null)
    
    scope.databaseConfiguration.username should be ("mypeople")
    scope.databaseConfiguration.password should be ("mypeople")
    scope.databaseConfiguration.connectionUrl should be ("jdbc:oracle:thin:@127.0.0.1:1521:XE")
    scope.databaseConfiguration.provider should be ("oracle")
    scope.databaseConfiguration.port should be (1521)
  }  
  
  it must " load email server information" in {
    
    val scope = ApplicationScope()
    
    assert(scope.emailConfiguration != null)
    
    scope.emailConfiguration.username should be ("myuser@domain.com")
    scope.emailConfiguration.password should be ("ultrasecurepassword")
    scope.emailConfiguration.locationUrl should be ("somesmtp.domain.com")
    
  }
  
  
}