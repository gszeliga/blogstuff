package com.covariantblabbering.dyi.scopes

import java.io.File
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.covariantblabbering.dyi.config.types._
import com.typesafe.config.ConfigException

trait ApplicationScope {
  def emailConfiguration: ServerConfiguration
  def defaultNotificationTemplate: File
  def databaseConfiguration: DatabaseConfiguration
}

object ApplicationScope {
  def apply(): ApplicationScope = {

    val config = ConfigFactory.load()

    new ApplicationScope {

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