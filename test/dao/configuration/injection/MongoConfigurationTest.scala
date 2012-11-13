package dao.configuration.injection

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.joda.time.DateTime

class MongoConfigurationTest extends FunSuite with ShouldMatchers {
    test("should return 'dynamic' given date when calling") {
        val future: Long = DateTime.now().plus(1000).getMillis

        val config = MongoConfiguration("test", () => future )

        config.now() should be (future)
    }
}