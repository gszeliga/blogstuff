package com.covariantblabbering.diy.providers

import scala.util.Try
import com.covariantblabbering.diy.config.types.Configuration

trait Provider[T] {
	def get: Try[Option[T]]
}