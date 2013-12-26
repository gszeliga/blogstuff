package com.covariantblabbering.dyi

import javax.sql.DataSource
import java.sql.ResultSet
import scala.util.Try
import scala.util.Success
import scala.util.Failure

package object repository {

  private[repository] object RepositoryTemplates {

    def get[T](ds: DataSource, sql: String)(rowToEntity: ResultSet => T): Try[Option[T]] = {

      try {
        val conn = ds.getConnection()
        val stm = conn.prepareStatement(sql)

        val rs = stm.executeQuery()

        if (rs.next()) {
          Success(Some(rowToEntity(rs)))
        } else {
          Success(None)
        }

      } catch {
        case e: Exception => Failure(e)
      }
    }
  }
}