package com.covariantblabbering.diy

import javax.sql.DataSource
import java.sql.ResultSet
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.{ None, Some }
import java.sql.Connection
import java.sql.Statement
import scala.collection.mutable.ListBuffer
import java.sql.PreparedStatement

package object repository {

  private[repository] object RepositoryTemplate {

    private[this] def mute[R](f: Function0[R]): Unit = {
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

    def execute[T](dataSource: Option[DataSource], sql: String)(traverse: ResultSet => Try[Option[T]]): JDBC[Option[T]] = {

      def closeSafely[P](objs: { def close(): Unit }*): P => Unit = { (p: P) =>

        for (obj <- objs) {
          mute(obj.close);
        }
        
      }

      dataSource match {

        case Some(ds) => {

          for (
              
            cn <- JDBC(() => ds.getConnection());
            stm <- JDBC(() => cn.prepareStatement(sql)) andIfHalted closeSafely(cn);
            rs <- JDBC(() => stm.executeQuery()) andIfHalted closeSafely(stm, cn);
            result <- JDBC(traverse(rs)) andIfHalted closeSafely(rs, stm, cn)

          ) yield {

            closeSafely(cn, stm, rs)
            
            result
          }
        }

        case None => new Halt(new RuntimeException("No datasource were found available"))

      }
    }

    def get2[T](dataSource: Option[DataSource], sql: String)(rowToEntity: ResultSet => T): JDBC[Option[T]] = {

      execute(dataSource, sql) { rs =>
        if (rs.next()) {
          lift1(rowToEntity)(rs)
        } else Success(None)
      }

    }

    def all2[T](dataSource: Option[DataSource], sql: String)(rowToEntity: ResultSet => T): JDBC[List[T]] = {

      val result = execute(dataSource, sql) { rs =>

        try {

          val ls = new ListBuffer[T]

          while (rs.next()) {
            lift1(rowToEntity)(rs) match {
              case Success(o) => o map { ls += }
              case Failure(e) => throw e
            }
          }

          Success(Some(ls.toList))

        } catch {
          case e: Throwable => Failure(e)
        }
      }

      result match {
        case Continue(Some(a)) => Continue(a)
        case Halt(e) => Halt(e)
      }

    }

    private[this] def executeVerbose[T](dataSource: Option[DataSource], sql: String)(traverse: ResultSet => Try[T]): Try[T] = {

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
                        case Success(Some(rs)) => try { traverse(rs) } finally {
                          mute(rs.close)
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

    def get[T](dataSource: Option[DataSource], sql: String)(rowToEntity: ResultSet => T): Try[Option[T]] = {

      executeVerbose[Option[T]](dataSource, sql) { rs =>
        if (rs.next()) {
          lift1(rowToEntity)(rs)
        } else Success(None)
      }
    }

    def all[T](dataSource: Option[DataSource], sql: String)(rowToEntity: ResultSet => T): Try[List[T]] = {

      executeVerbose[List[T]](dataSource, sql) { rs =>

        try {

          val ls = new ListBuffer[T]

          while (rs.next()) {
            lift1(rowToEntity)(rs) match {
              case Success(o) => o map { ls += }
              case Failure(e) => throw e
            }
          }

          Success(ls.toList)

        } catch {
          case e: Throwable => Failure(e)
        }
      }
    }
  }
}