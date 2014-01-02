package com.covariantblabbering.diy.repository

import com.covariantblabbering.diy.providers.Provider
import javax.sql.DataSource
import scala.util.Try
import com.covariantblabbering.diy.config.types.DatabaseConfiguration

object DataSourceProvider {

  def apply(config: DatabaseConfiguration): Provider[DataSource] =  ???
}