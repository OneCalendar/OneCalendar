import sbt._
import sbt.Keys._
import play.Project._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.mongodb" %% "casbah" % "2.5.1",

	    "org.webjars" %% "webjars-play" % "2.2.0",
        "org.webjars" % "jquery" % "2.1.0-3",
        "org.webjars" % "foundation" % "5.2.2",
        "org.webjars" % "foundation-icon-fonts" % "d596a3cfb3",
        "org.webjars" % "modernizr" % "2.7.1",
        "org.webjars" % "normalize.css" % "2.1.3",
        "org.webjars" % "momentjs" % "2.6.0",
	    "org.webjars" % "chosen"   % "1.1.0",

	    "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        ),
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.0" % "test",
        "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.1" % "test"
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