package wafna.radius

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestPacketSerialization extends FunSuite {
  val packetType = PacketType.AccessRequest
  val packetId: Byte = 99
  val userName: String = "Billy Bob"
  val nasId: String = "Go NAS!"
  val authenticator = (0 until 16).foldLeft(new Array[Byte](16)) { (a, i) => a(i) = i.toByte ; a }
  def testAuth(auth: Array[Byte]): Unit = {
    assert((0 until 16).map(v => auth(v) == v).forall(v => v), "authenticator")
  }
  def createPacket(attrs: List[StdAttr]): RadiusPacket = {
    new RadiusPacket(packetType, packetId, authenticator, attrs)
  }
  def testPacket(packet1: RadiusPacket): Unit = {
    val buffer = new Array[Byte](Short.MaxValue)
    packet1.write(buffer)
    val packet2 = RadiusPacket read buffer
    assertResult(packet1.packetType, "code")(packet2.packetType)
    assertResult(packet1.packetId, "id")(packet2.packetId)
    val attrs1 = packet1.attributes
    val attrs2 = packet2.attributes
    assertResult(attrs1.length, "length of attrs")(attrs2.length)
    attrs1.zip(attrs2) map { pair =>
      val a1 = pair._1
      val a2 = pair._2
      assertResult(a1.attrType, "attr type")(a2.attrType)
      assertResult(a1.data.length, s"attr value length: ${a1.attrType}")(a2.data.length)
    }
  }
  test("no attrs") {
    testPacket(createPacket(Nil))
  }
  test("user name") {
    testPacket(createPacket(List(new StdAttr(StdAttrType.UserName, userName))))
  }
  test("user name and nas id") {
    testPacket(createPacket(List(new StdAttr(StdAttrType.UserName, userName), new StdAttr(StdAttrType.NASIdentifier, nasId))))
  }
  test("vendor specific") {
    testPacket(createPacket(List(new VendorSpecific(Vendor.AcmeWidgets, )))
  }
}
