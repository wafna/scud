package wafna.udp.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestConfig extends FunSuite {
  test("config") {
    val port = UDPConf.port
    // we're not interested in the particular value of the port so much as that we read it correctly from the config.
    assertResult(4445, "port")(port)
  }
}
