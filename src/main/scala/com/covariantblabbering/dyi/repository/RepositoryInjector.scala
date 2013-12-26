package com.covariantblabbering.dyi.repository

import com.covariantblabbering.dyi.scopes.ApplicationScope
import javax.sql.DataSource

object RepositoryInjector {
  def injectDataSource(scope: ApplicationScope): DataSource = null
  def injectHumanRepository(scope: ApplicationScope): HumanRepository = new HumanRepositoryImpl(injectDataSource(scope))
}