package com.covariantblabbering.diy.repository

import com.covariantblabbering.diy.scopes.ApplicationScope
import javax.sql.DataSource

object RepositoryInjector {
  def injectDataSourceProvider(scope: ApplicationScope) = DataSourceProvider(scope.databaseConfiguration)
  def injectDataSource(scope: ApplicationScope): Option[DataSource] = scope.dataSource(injectDataSourceProvider(scope))
  def injectPersonRepository(scope: ApplicationScope): PersonRepository = new PersonRepositoryImpl(injectDataSource(scope))
}