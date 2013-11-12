package com.covariantblabbering.structural_typing
package com.covariantblabbering.ducktype

import java.io.FileInputStream
import java.io.FileReader
import java.io.BufferedReader
 
object structural_types {
  
  tryUsing(new BufferedReader(new FileReader("/home/guillermo/.bashrc"))) { stream =>

    println(stream.readLine())
    
    throw new Exception("...yeah....I just failed...")

  } withFallback { e =>  println("Stream reading process just failed!! = " + e)
                                                  //> loaning resource...
                                                  //| # ~/.bashrc: executed by bash(1) for non-login shells.
                                                  //| closing...
                                                  //| Stream reading process just failed!! = java.lang.Exception: ...yeah....I jus
                                                  //| t failed...
  }

}