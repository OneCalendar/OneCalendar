import sbt._
import sbt.Keys._
import PlayProject._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.8" % "test",
        "org.mongodb" %% "casbah" % "2.4.1",
        "org.mockito" % "mockito-all" % "1.9.0" % "test",
        "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
            )
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(

        testOptions in Test := Nil, //to run scalatest in play2 console arghhhh!!!

        // available test resources in play2 classpath
        unmanagedClasspath in Test <+= ( baseDirectory ) map {
            bd => Attributed.blank(bd / "test")
        }
    )
}