import sbt._
import Keys._
import PlayProject._


object ApplicationBuild extends Build {

    val appName = "OneCalendar"
    val appVersion = "1.0-SNAPSHOT"

    resolvers += "oss-sonatype" at "http://oss.sonatype.org/content/groups/public"
    resolvers += "oss-sonatype-releases" at "http://oss.sonatype.org/content/repositories/releases/"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.8" % "test",
        "org.mongodb" % "mongo-java-driver" % "2.7.3",
        "com.codahale" % "jerkson_2.9.1" % "0.5.0",
        "org.mockito" % "mockito-all" % "1.9.0" % "test",
        "org.mnode.ical4j" % "ical4j" % "1.0.3" excludeAll(
            ExclusionRule(organization = "org.slf4j"),
            ExclusionRule(organization = "commons-logging")
        )
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
        testOptions in Test := Nil,  //to run scalatest in play2 console arghhhh!!!

        // available test resources in play2 classpath
        unmanagedClasspath in Test <+= (baseDirectory) map { bd => Attributed.blank(bd / "test") }
    )

}
