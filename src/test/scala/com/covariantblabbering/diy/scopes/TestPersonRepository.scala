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

@RunWith(classOf[JUnitRunner])
class TestPersonRepository extends FlatSpec with Matchers {

  behavior of "PersonRepository"

  val scope = ApplicationScope()

  "If I search a person using its unique id I " must "retrieve the related Person instance" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    repository.get("1") shouldBe a[Success[Some[Person]]]
   
  }

  "If I list all existing people a non-empty list " must "be returned" in {

    val repository = RepositoryInjector.injectPersonRepository(scope)

    val result = repository.all

    result shouldBe a[Success[List[Person]]]
    result.get should not be empty
    result.get should have size (2)

  }

}