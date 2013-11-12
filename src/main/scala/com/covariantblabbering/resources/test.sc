package com.covariantblabbering.resources

object test {

	class ReleseableResource{
		def release() = println("My resource has been released...")
	}
	
	class ClosableResource{
		def close() = println("My resource has been closed....")
	}

  using(new ClosableResource) { stream =>
  
    throw new Exception("..ups....")

  } withFallback { e => println("just failed: " + e)}
                                                  //> executiong close() method....
                                                  //| My resource has been closed....
                                                  //| just failed: java.lang.Exception: ..ups....
 
  using(new ReleseableResource()) { stream =>

    throw new Exception("..ups....")

  } withFallback { e => println("just failed: " + e)}
                                                  //> executing release() method....
                                                  //| My resource has been released...
                                                  //| just failed: java.lang.Exception: ..ups....

}