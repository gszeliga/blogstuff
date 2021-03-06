package com.covariantblabbering.diy

import com.typesafe.config.Config
import com.typesafe.config.ConfigException

package object scopes {

  implicit class EnrichedConfig(val underlying: Config) extends AnyVal {
    def getOptionalString(key: String): Option[String] = {
      try {
        Some(underlying.getString(key))
      } catch {
        case e: ConfigException.Missing => None
      }
    }
    
    def getOptionalInt(key: String): Option[Int] = {
      try {
        Some(underlying.getInt(key))
      } catch {
        case e: ConfigException.Missing => None
      }
    }    
    
  }

}