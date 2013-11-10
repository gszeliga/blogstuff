package com.covariantblabbering.structural_typing
package com.covariantblabbering.ducktype

import java.io.FileInputStream
import java.io.FileReader
import java.io.BufferedReader

object structural_types {
  
  tryUsing(new BufferedReader(new FileReader("/home/guillermo/.bashrc"))) { stream =>

    stream.readLine()
    
    throw new Exception("...yeah....I just failed...")

  } withFallback { (stream, e) =>  println("Stream reading process just failed!! = " + e)
                                                  //> loaning resource...
                                                  //| closing...
                                                  //| Stream reading process just failed!! = java.lang.Exception: ...yeah....I jus
                                                  //| t failed...
  }

}