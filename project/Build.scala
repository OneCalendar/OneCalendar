import sbt._
import sbt.Keys._
import play.Project._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.mongodb"               %% "casbah"                 % "2.5.1",
        "org.mnode.ical4j"          %  "ical4j"                 % "1.0.3"       excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        ),
        "org.reactivemongo"         %% "reactivemongo"          % "0.10.0",
        "org.reactivemongo"         %% "play2-reactivemongo"    % "0.10.2",

        "org.scalatest"             %% "scalatest"              % "2.1.0"       % "test",
        "org.mockito"               % "mockito-all"             % "1.9.0"       % "test",
        "com.github.simplyscala"    %% "scalatest-embedmongo"   % "0.2.1"       % "test"
    )
    val main = play.Project(appName, appVersion, appDependencies).settings(

        // available test resources in play2 classpath
        unmanagedClasspath in Test <+= ( baseDirectory ) map {
            bd => Attributed.blank(bd / "test")
        }
    )
}