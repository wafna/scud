package wafna.radius.protocol

import java.nio.ByteBuffer

import wafna.udp.util.{WriteBuffer, ReadBuffer}

object Attribute {
  def read(buffer: ReadBuffer): Attribute = {
    val code = buffer.readByte()
    val length = buffer.readByte()
    val data = buffer.readBytes(length - 2)
    new Attribute(code, data)
  }
}
class Attribute(val code: Byte, val data: ByteBuffer) {
  def this(code: Byte, data: Array[Byte]) = this(code, ByteBuffer.wrap(data).asReadOnlyBuffer())
  def this(code: Byte, data: String, encoding: String = "utf-8") = this(code, ByteBuffer.wrap(data.getBytes(encoding)).asReadOnlyBuffer())
  def write(buffer: WriteBuffer): Unit = {
    buffer.writeByte(code)
    buffer.writeByte(2 + data.capacity())
    buffer.writeBytes(data)
  }
}

/**
 * Unstructured vendor data
 */
class VendorData(val vendorId: Int, val data: ByteBuffer) {
  def this(vendorId: Int, data: Array[Byte]) = this(vendorId, ByteBuffer.wrap(data))
  def readSubAttrs(): List[Attribute] = {
    var attributes = List[Attribute]()

  }
}
object Packet {
  /**
   * Read a radius packet from a buffer.
   */
  def read(buffer: Array[Byte]): Packet = {
    val rb = new ReadBuffer(buffer, 0)
    val code = rb.readByte()
    val id = rb.readByte()
    val length = rb.readShort()
    val authenticator = rb.readBytes(16)
    // the rest will be the attributes
    var attributes: List[Attribute] = Nil
    while (rb.position < length) {
      val attrCode = rb.readByte()
      val dataLength = rb.readByte()
      val data = rb.readBytes(dataLength)
      attributes ::= {
        new Attribute(attrCode, data)
      }
    }
    // If this trips we've surely run over or the while loop wouldn't have terminated normally.
    if (length!= rb.position) sys error s"Incorrect read of packet: expected $length but read ${rb.position}."
    new Packet(code, id, authenticator, attributes.reverse)
  }
  def write(buffer: Array[Byte], code: Byte , packetId: Byte, authenticator: Array[Byte], attributes: Seq[Attribute]): Unit = {
    val wb = new WriteBuffer(buffer, 0)
    val length = 20 + attributes.foldLeft(0) { (c, attr) => c + 2 + attr.data.length }
    // This is the limit according to RFC2138.  Thus, the entire buffer need only be 4098 bytes (which is much shorter than the max UDP packet size).
    if (length > 4096) sys error s"Radius packet is too large: $length"
    wb.writeByte(code)
    wb.writeByte(packetId)
    wb.writeShort(length)
    wb.writeBytes(authenticator)
    attributes.foreach(_ write wb)
    val pos = wb.position
    // just checking!
    if (pos != length) sys error s"Incorrect write of packet: $pos $length"
  }
}

class Packet(val code: Byte , val packetId: Byte, val authenticator: Array[Byte], val attributes: Seq[Attribute]) {
  def write(bytes: Array[Byte]): Unit = Packet.write(bytes, code, packetId, authenticator, attributes)
}
