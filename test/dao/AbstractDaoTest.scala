package dao

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.{MustMatchers, ShouldMatchers}
import org.fest.util.Files
import java.util.ArrayList
import java.io.{InputStreamReader, BufferedReader, File}
import org.scalatest.FunSuite
import com.mongodb._

/**
 * User: amira
 * Date: 12/03/12
 */

class AbstractDaoTest extends FunSuite with BeforeAndAfterAll with ShouldMatchers with MustMatchers {

    def ensureDbPathDoesNotExists(): File = {
        var dbPath: File = new File("target/data")
        if (dbPath.exists()) {
            Files.delete(dbPath)
            dbPath.exists() should be(false)
        }
        dbPath
    }

    def startMongoDbAsDaemon(): ArrayList[String] = {
        var processBuilder: ProcessBuilder = new ProcessBuilder("mongod", "--dbpath", "data/", "--fork", "--logpath", "data/mongodb.log")
        processBuilder.directory(new File("target"))
        processBuilder.redirectErrorStream(true)
        var pwd: Process = processBuilder.start()
        var outputReader: BufferedReader = new BufferedReader(new InputStreamReader(pwd.getInputStream()))
        var output: String = outputReader.readLine()
        val lines: ArrayList[String] = new ArrayList()
        while (output != null) {
            lines.add(output)
            output = outputReader.readLine()
        }
        pwd.waitFor()
        pwd.exitValue() should be(0)
        lines
    }

    def ConnectionToMongodbIsPossible() {
        var server: Mongo = null
        try {
            while (server == null) {
                Thread.sleep(250);
                server = new MongoURI("mongodb://127.0.0.1").connect()
            }
            server.getDatabaseNames.size() //should be(2)
        } finally {
            server.close()
        }
    }

    override def beforeAll() {
        var dbPath: File = ensureDbPathDoesNotExists()
        dbPath.mkdir() should be(true)
        var lines: ArrayList[String] = startMongoDbAsDaemon()
        lines.get(0) should startWith("forked process: ")
        lines.get(1) should startWith("all output going to: ")
        lines.get(1) should endWith("data/mongodb.log")
        lines.size() should be(2)
        ConnectionToMongodbIsPossible()
    }

    override def afterAll() {
        var mongo: Mongo = new Mongo()
        try {

            var db: DB = mongo.getDB("admin")
            val shutDownResult: CommandResult = db.command(new BasicDBObject("shutdown", 1))
            shutDownResult.throwOnError
            fail("Excepcting to loose mongodb connection on shutdown.")
        } catch {
            case e: Throwable => e.getMessage should be("can't call something")
        } finally {
            mongo.close()
            ensureDbPathDoesNotExists()
        }
    }

}

