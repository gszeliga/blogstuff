package com.covariantblabbering.resources

object test {

	class ReleseableResource{
		def release() = println("My resource has been released")
	}

  using(new java.io.FileInputStream("/home/guillermo/.bashrc")) { stream =>
  
    stream.read()

    throw new Exception("..ups....")

  } withFallback { (r, e) => println("just failed: " + e)}
                                                  //> executiong close method....
                                                  //| just failed: java.lang.Exception: ..ups....

  using(new ReleseableResource()) { stream =>

    throw new Exception("..ups....")

  } withFallback { (r, e) => println("just failed: " + e)}
                                                  //> execution release method....
                                                  //| My resource has been released
                                                  //| just failed: java.lang.Exception: ..ups....

}