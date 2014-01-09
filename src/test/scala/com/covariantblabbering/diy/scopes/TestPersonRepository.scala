package com.covariantblabbering.diy.scopes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.covariantblabbering.diy.repository.RepositoryInjector
import com.covariantblabbering.diy.domain.Person
import scala.util.Success
import com.covariantblabbering.diy.repository.PersonRepository
import com.covariantblabbering.diy.repository.PersonRepositoryImpl
import com.covariantblabbering.diy.repository.Continue
import com.covariantblabbering.diy.repository.Halt
import scala.util.Failure

@RunWith(classOf[JUnitRunner])
class TestPersonRepository extends FlatSpec with Matchers {

  behavior of "PersonRepository"

  val scope = ApplicationScope()

  "If I search a person using its unique id I " must "retrieve the related Person instance" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    repository.get("1") shouldBe a [Success[Some[Person]]]
   
  }

  "(Alternative) If I search a person using its unique id I " must "retrieve the related Person instance" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    repository.get2("1") shouldBe a [Success[Some[Person]]]
   
  }  
  
  "(Alternative) If my row mapping functions is wrong then " must "must fail" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    val result = repository.getWithErrorsOnMapping("1") 
    
    result shouldBe a [Failure[Exception]]
   
  }  
  
  "If I list all existing people a non-empty list " must "be returned" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    val result = repository.all

    result shouldBe a[Success[List[Person]]]
    result.get should not be empty
    result.get should have size (2)

  }
  
  "(Alternative) If I list all existing people a non-empty list " must "be returned" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    val result = repository.all2

    result shouldBe a [Success[List[Person]]]
    result.get should not be empty
    result.get should have size (2)

  }  

}