import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "OneCalendar"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.scalatest" %% "scalatest" % "test",
      "org.joda" % "joda-convert" % "1.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
      // Add your own project settings here      
    )

}
