name := "test2"

version := "1.0"

scalaVersion := "2.12.3"



libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.4" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.9" % Test
)

libraryDependencies +=
  "com.typesafe.akka" %% "akka-cluster" % "2.5.4"


libraryDependencies +=
  "com.typesafe.akka" %% "akka-cluster-sharding" %  "2.5.4"


libraryDependencies +=
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.4"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4"


libraryDependencies ++= Seq(
  "org.iq80.leveldb"           %  "leveldb" % "0.7",
  "org.fusesource.leveldbjni"  %  "leveldbjni-all"  % "1.8",
  "org.apache.thrift" % "libthrift" % "0.9.3" % "compile"

)

// https://mvnrepository.com/artifact/com.typesafe.play/play-json_2.11
//libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.6.3"
libraryDependencies +="com.typesafe.play" %% "play-json" % "2.6.3"





libraryDependencies += "com.github.etaty" %% "rediscala" % "1.8.0"

//hootsuite redis dependencies
resolvers += Resolver.jcenterRepo // Adds Bintray to resolvers for akka-persistence-redis and rediscala
libraryDependencies ++= Seq("com.hootsuite" %% "akka-persistence-redis" % "0.7.0")

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.17"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

//addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.2")