import sbt._
import sbt.Keys._
import play.Project._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.mongodb" %% "casbah" % "2.5.1",
        "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        ),
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