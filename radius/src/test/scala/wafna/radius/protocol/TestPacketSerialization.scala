package wafna.radius.protocol

import java.nio.ByteBuffer
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import StdAttrType._

@RunWith(classOf[JUnitRunner])
class TestPacketSerialization extends FunSuite {
  val packetType = PacketType.AccessRequest.code
  val packetId: Byte = 99
  val userName: String = "Billy Bob"
  val nasId: String = "Go NAS!"
  val authenticator = (0 until 16).foldLeft(new Array[Byte](16)) { (a, i) => a(i) = i.toByte ; a }
  def testAuth(auth: Array[Byte]): Unit = {
    assert((0 until 16).map(v => auth(v) == v).forall(v => v), "authenticator")
  }
  def createPacket(attrs: Attribute *): Packet = {
    new Packet(packetType, packetId, authenticator, attrs)
  }
  def testPacket(packet1: Packet): Unit = {
    val buffer = new Array[Byte](Short.MaxValue)
    packet1.write(buffer)
    val packet2 = Packet read buffer
    assertResult(packet1.code, "code")(packet2.code)
    assertResult(packet1.packetId, "id")(packet2.packetId)
    val attrs1 = packet1.attributes
    val attrs2 = packet2.attributes
    assertResult(attrs1.length, "length of attrs")(attrs2.length)
    attrs1.zip(attrs2) map { pair =>
      val a1 = pair._1
      val a2 = pair._2
      assertResult(a1.code, "attr type")(a2.code)
      assertResult(a1.data.capacity(), s"attr value length: ${a1.code}")(a2.data.capacity())
    }
  }
//  test("no attrs") {
//    testPacket(createPacket())
//  }
  test("user name") {
    testPacket(createPacket(new Attribute(UserName.code, userName)))
  }
//  test("user name and nas id") {
//    testPacket(createPacket(new Attribute(UserName.code, userName), new Attribute(NASIdentifier.code, nasId)))
//  }
//  test("vendor specific") {
//    testPacket(createPacket(List(new VendorSpecific(Vendor.AcmeWidgets, )))
//  }
}
