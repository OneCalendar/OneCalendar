import sbt._
import Keys._
import PlayProject._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    resolvers += "oss-sonatype" at "http://oss.sonatype.org/content/groups/public"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.7.1" % "test",
        "org.mongodb" % "mongo-java-driver" % "2.7.3",
        "com.mongodb.casbah" % "casbah_2.9.1" % "2.1.5-1", // to replace java-driver
        "com.codahale" % "jerkson_2.9.1" % "0.5.0",
        "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        )

    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
        testOptions in Test := Nil  //to run scalatest in play2 console arghhhh!!!
    )

}
