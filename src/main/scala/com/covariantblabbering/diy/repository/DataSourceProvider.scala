package com.covariantblabbering.diy.repository

import com.covariantblabbering.diy.providers.Provider
import javax.sql.DataSource
import scala.util.Try
import com.covariantblabbering.diy.config.types.DatabaseConfiguration
import org.apache.derby.jdbc.ClientConnectionPoolDataSource
import oracle.jdbc.pool.OracleDataSource
import com.covariantblabbering.diy.providers.Provider
import scala.util.{ Success, Failure }

object AvailableProviders extends Enumeration {

  val Oracle = MyValue("oracle")
  val Derby = MyValue("derby")

  def MyValue(name: String): Value with Matching =
    new Val(nextId, name) with Matching

  // enables matching against all AvailableProviders.Values
  def unapply(s: String): Option[Value] =
    values.find(s == _.toString)

  trait Matching {
    // enables matching against a particular AvailableProviders.Value
    def unapply(s: String): Boolean =
      (s == toString)
  }

}

private class DerbyProvider(val config: DatabaseConfiguration) extends Provider[ClientConnectionPoolDataSource] {
  def get: Try[Option[ClientConnectionPoolDataSource]] = {

    val pool = new ClientConnectionPoolDataSource();

    pool.setUser(config.username);
    pool.setPassword(config.password);
    pool.setPortNumber(config.port);
    pool.setDatabaseName(config.databaseName);

    Success(Some(pool))

  }
}

private class OracleProvider(val config: DatabaseConfiguration) extends Provider[OracleDataSource] {
  def get: Try[Option[OracleDataSource]] = {

    try {
      val pool = new OracleDataSource();

      pool.setURL(config.connectionUrl);
      pool.setUser(config.username);
      pool.setPassword(config.password);
      pool.setConnectionCachingEnabled(true);
      pool.setConnectionCacheName("my-diy-cache");
      Success(Some(pool))
    } catch {
      case e: Exception => Failure(e)
    }

  }
}

object DataSourceProvider {

  def apply(config: DatabaseConfiguration): Option[Provider[DataSource]] = {
    config.provider match {
      case AvailableProviders.Oracle() => Some(new OracleProvider(config))
      case AvailableProviders.Derby() => Some(new DerbyProvider(config))
      case _ => None
    }
  }
}