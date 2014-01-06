package com.covariantblabbering.diy.config

import java.net.URL
import java.io.File

package object types {

  trait Configuration
  
  trait ServerConfiguration extends Configuration{
    def username: String
    def password: String
    def locationUrl: String
  }

  trait DatabaseConfiguration extends Configuration{

    def username: String
    def password: String
    def connectionUrl: String
    def provider: String
    def port: Int
    def databaseName: String
  }

  object DatabaseConfiguration {

    def apply(usr: Option[String], pwd: Option[String], url: Option[String], prov: Option[String], dbPort: Option[Int], dbName: Option[String]): DatabaseConfiguration = {

      assert(usr.isDefined, "Database username is missing")
      assert(pwd.isDefined, "Database password is missing")
      assert(url.isDefined, "Database url is missing")
      assert(prov.isDefined, "Database provider is missing")
      assert(dbPort.isDefined, "Database port is missing")
      assert(dbName.isDefined, "Database name is missing")
      
      new DatabaseConfiguration {
        def username = usr.get
        def password = pwd.get
        def connectionUrl = url.get
        def provider = prov.get
        def port = dbPort.get
        def databaseName = dbName.get
      }

    }
  }

  object ServerConfiguration {

    def apply(usr: Option[String], pwd: Option[String], url: Option[String]): ServerConfiguration = {
      
      assert(usr.isDefined)
      assert(pwd.isDefined)
      assert(url.isDefined)
      
      new ServerConfiguration {
        def username = usr.get
        def password = pwd.get
        def locationUrl = url.get
      }

    }
  }
}
