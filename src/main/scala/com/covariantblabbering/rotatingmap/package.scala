package com.covariantblabbering

/**
 * Created by guillermo on 12/11/14.
 */
package object rotatingmap {
  type OnExpiration[K,V] = ((K,V)) => Unit
}
