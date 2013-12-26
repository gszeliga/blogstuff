package com.covariantblabbering.dyi.config

import java.net.URL
import java.io.File

package types {

  trait ServerConfiguration {
    def username: String
    def password: String
    def locationUrl: String
  }

  trait DatabaseConfiguration {

    def username: String
    def password: String
    def connectionUrl: String
    def provider: String
  }

  object DatabaseConfiguration {

    def apply(usr: Option[String], pwd: Option[String], url: Option[String], prov: Option[String]): DatabaseConfiguration = {

      assert(usr.isDefined, "Database username is missing")
      assert(pwd.isDefined, "Database password is missing")
      assert(url.isDefined, "Database url is missing")
      assert(prov.isDefined, "Database provider is missing")
      
      new DatabaseConfiguration {
        def username = usr.get
        def password = pwd.get
        def connectionUrl = url.get
        def provider = prov.get
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
