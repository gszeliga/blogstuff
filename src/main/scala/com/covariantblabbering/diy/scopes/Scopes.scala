package com.covariantblabbering.diy.scopes

import java.io.File
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigException
import com.covariantblabbering.diy.config.types._
import com.covariantblabbering.diy.scopes._
import com.covariantblabbering.diy.providers.Provider
import scala.util.{ Success, Failure }
import javax.sql.DataSource

class ScopeCache[R] {

  private var _full: Boolean = false
  private var _cache: Option[R] = None

  def get(p: Provider[R]): Option[R] = {
    synchronized {
      if (!_full) {
        p.get match {
          case Success(v) => {
            _cache = v
            _full = true
          }
          case Failure(e) => println("Cache initialization failed")
        }
      }
      _cache
    }
  }
}

trait ApplicationScope {
  def emailConfiguration: ServerConfiguration
  def defaultNotificationTemplate: File
  def databaseConfiguration: DatabaseConfiguration
  def dataSource(p: Provider[DataSource]): Option[DataSource]
}

object ApplicationScope {
  def apply(): ApplicationScope = {

    val config = ConfigFactory.load()

    new ApplicationScope {

      private val dataSourceCache = new ScopeCache[DataSource]()

      def dataSource(p: Provider[DataSource]): Option[DataSource] = dataSourceCache.get(p)

      def emailConfiguration = ServerConfiguration(config.getOptionalString("my-diy-app.email.username"),
        config.getOptionalString("my-diy-app.email.password"),
        config.getOptionalString("my-diy-app.email.url"))

      def defaultNotificationTemplate = null

      def databaseConfiguration = DatabaseConfiguration(config.getOptionalString("my-diy-app.datasource.username"),
        config.getOptionalString("my-diy-app.datasource.password"),
        config.getOptionalString("my-diy-app.datasource.url"),
        config.getOptionalString("my-diy-app.datasource.type"))
    }
  }
}