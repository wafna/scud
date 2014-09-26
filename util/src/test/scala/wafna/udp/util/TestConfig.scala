package wafna.udp.util

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestConfig extends FunSuite {
  test("config") {
    val config = ConfigFactory.load(this.getClass.getClassLoader, "app.conf")
    val port = config.getInt("port")
    // we're not interested in the particular value of the port so much as that we read it correctly from the config.
    assertResult(4445, "port")(port)
  }
}
