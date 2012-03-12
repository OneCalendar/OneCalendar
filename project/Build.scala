import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "OneCalendar"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.scalatest" %% "scalatest" % "1.7.1" % "test",
      "org.joda" % "joda-convert" % "1.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
