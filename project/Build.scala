import sbt._
import sbt.Keys._
import play.Project._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    resolvers += "Jerkson repo" at "http://repo.codahale.com"

    val appDependencies = Seq(
        "org.mongodb" % "casbah_2.9.2" % "2.4.1",
        "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        ),
        "com.codahale" % "jerkson_2.9.1" % "0.5.0",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.0" % "test"
    )
                                                                //, mainLang = SCALA
    val main = play.Project(appName, appVersion, appDependencies).settings(

        testOptions in Test := Nil, //to run scalatest in play2 console arghhhh!!!

        // available test resources in play2 classpath
        unmanagedClasspath in Test <+= ( baseDirectory ) map {
            bd => Attributed.blank(bd / "test")
        }
    )
}