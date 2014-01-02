package com.covariantblabbering.diy

import javax.sql.DataSource
import java.sql.ResultSet
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.{ None, Some }
import java.sql.Connection
import java.sql.Statement

package object repository {

  private[repository] object RepositoryTemplate {

    private[this] def mute[R](f: Function0[R]) = {
      try {
        f()
      } catch {
        case ignored: Throwable =>
      }
    }

    private[this] def lift[R](f: Function0[R]) = () => {

      try {
        Success(Option(f()))
      } catch {
        case e: Throwable => Failure(e)
      }
    }

    private[this] def lift1[P, R](f: Function1[P, R]) = (p1: P) => {
      try {
        Success(Option(f(p1)))
      } catch {
        case e: Throwable => Failure(e)
      }
    }

    def get[T](dataSource: Option[DataSource], sql: String)(rowToEntity: ResultSet => T): Try[Option[T]] = {

      dataSource match {
        case Some(ds) => {
          
          lift(ds.getConnection)() match {

            case Failure(e) => Failure(e)
            case Success(None) => Failure(new RuntimeException("Connection could not be retrieved"))
            case Success(Some(c)) => {

              try {
                lift1(c.prepareStatement)(sql) match {

                  case Failure(e) => Failure(e)
                  case Success(None) => Failure(new RuntimeException("Statement could not be opened"))
                  case Success(Some(stm)) => {

                    try {
                      lift(stm.executeQuery)() match {

                        case Failure(e) => Failure(e)
                        case Success(None) => Failure(new RuntimeException("Excepted resultset is missing"))
                        case Success(Some(rs)) => {

                          try {
                            if (rs.next()) {
                              lift1(rowToEntity)(rs)
                            } else Success(None)
                          } finally {
                            mute(rs.close)
                          }
                        }

                      }
                    } finally {
                      mute(stm.close)
                    }
                  }
                }
              } finally {
                mute(c.close)
              }
            }
          }
        }

        case None => Failure(new RuntimeException("Provided datasource is empty"))

      }

    }
  }
}