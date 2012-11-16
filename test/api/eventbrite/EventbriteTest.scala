/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api.eventbrite

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.lang.IllegalArgumentException

class EventbriteTest extends FunSuite with ShouldMatchers {

    test("should parse real eventbrite stream without exception") {
        Eventbrite.request("scala", defaultTags = Nil, originalStream = "originalStream")
    }

    test("request with country filter should not throw exception") {
        Eventbrite.request("java", Some("FR"), defaultTags = Nil, originalStream = "originalStream")
    }

    test("request with bad license key should throw IllegalArgumentException") {
        val thrown = evaluating {
            Eventbrite.request(keyWord = "toto", defaultTags = Nil, licenseKey = "invalidLicenceKey", originalStream = "originalStream")
        } should produce [IllegalArgumentException]
        thrown.getMessage should (include("application key") and include ("not valid"))
    }

}