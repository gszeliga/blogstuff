package com.covariantblabbering.dyi.repository

import com.covariantblabbering.dyi.config.types.DatabaseConfiguration
import com.covariantblabbering.dyi.domain.Human
import javax.sql.DataSource
import scala.util.Try
import java.sql.Connection
import scala.util.Failure
import scala.util.Success
import java.sql.ResultSet

trait HumanRepository {
  def get(id: String): Try[Option[Human]]
}

private[repository] class HumanRepositoryImpl(private val datasource: DataSource) extends HumanRepository {
  def get(id: String): Try[Option[Human]] = {

    RepositoryTemplates.get(datasource, s"select * from humans where id=$id") { rs =>
      new Human(rs.getString("name"), rs.getString("lastname"))
    }

  }
}