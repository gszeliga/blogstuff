package com.covariantblabbering.diy.repository

import com.covariantblabbering.diy.domain.Person
import javax.sql.DataSource
import scala.util.Try
import java.sql.Connection
import scala.util.Failure
import scala.util.Success
import java.sql.ResultSet

trait PersonRepository {
  def get(id: String): Try[Option[Person]]
  def get2(id: String): Try[Option[Person]]
  def getWithErrorsOnMapping(id: String): Try[Option[Person]]
  def all: Try[List[Person]]
  def all2: Try[List[Person]]
}

private[repository] class PersonRepositoryImpl(private val dataSource: Option[DataSource]) extends PersonRepository {

  def get2(id: String): Try[Option[Person]] = {

    RepositoryTemplate.get2(dataSource, s"select * from people where id=$id") {
      rs =>
        new Person(rs.getString("name"), rs.getString("lastname"))
    } 
  }

  def all2: Try[List[Person]] = {
    RepositoryTemplate.all2(dataSource, "select * from people") {
      rs =>
        new Person(rs.getString("name"), rs.getString("lastname"))
    } 
  }

  def getWithErrorsOnMapping(id: String): Try[Option[Person]] = {

    RepositoryTemplate.get2(dataSource, s"select * from people where id=$id") {
      rs =>
        new Person(rs.getString("nam"), rs.getString("lastnam"))
    }
  } 

  def get(id: String): Try[Option[Person]] = {

    RepositoryTemplate.get(dataSource, s"select * from people where id=$id") {
      rs =>
        new Person(rs.getString("name"), rs.getString("lastname"))
    }
  }

  def all: Try[List[Person]] = {
    RepositoryTemplate.all(dataSource, "select * from people") {
      rs =>
        new Person(rs.getString("name"), rs.getString("lastname"))
    }
  }

}