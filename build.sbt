name := "blogstuff"

version := "1.0"

scalaVersion := "2.11.3"

libraryDependencies ++= {
  Seq(
    "org.apache.derby" % "derbyclient" % "10.11.1.1",
    "com.oracle" % "ojdbc6" % "11.2.0.2.0",
    "com.typesafe" % "config" % "1.0.2" withSources(),
    //"org.scala-lang.modules" % "scala-async" % "0.9.2",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % Test withSources(),
    "org.hamcrest" % "hamcrest-all" % "1.3" % Test withSources(),
    "junit" % "junit" % "4.8.1" % Test
  )
}

resolvers += Resolver.mavenLocal
