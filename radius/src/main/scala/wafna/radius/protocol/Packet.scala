package wafna.radius.protocol

import java.nio.{ByteOrder, ByteBuffer}

import wafna.radius.ByteBufferEx._

object Attribute {
  def read(buffer: ByteBuffer): Attribute = {
    val code = buffer.get()
    val length = buffer.get()
    val data = buffer.getArray(length - 2)
    new Attribute(code, data)
  }
  def readAll(buffer: ByteBuffer): List[Attribute] = {
    // the rest will be the attributes
    var attributes: List[Attribute] = Nil
    while (0 < buffer.remaining()) {
      attributes ::= read(buffer)
    }
    attributes
  }
}
/**
 * Attributes both at the packet level (i.e. the standard attributes) and within those attributes' values follow this
 * format.  We use a ByteBuffer here so that we can supply this value either as an array or map it onto an existing
 * array (esp. the read buffer from UDP).
 * @param code Identifies the type of the attribute.
 * @param data This may have some required structure base on the code.
 */
class Attribute(val code: Byte, val data: ByteBuffer) {
  def this(code: Byte, data: Array[Byte]) = this(code, ByteBuffer.wrap(data).asReadOnlyBuffer())
  def this(code: Byte, data: String, encoding: String = "utf-8") = this(code, ByteBuffer.wrap(data.getBytes(encoding)).asReadOnlyBuffer())
  /**
   * @return the number of bytes required to serialize the attribute
   */
  def length = 2 + data.limit()
  def write(buffer: ByteBuffer): ByteBuffer = {
    buffer.put(code)
    buffer.putIntAsByte(1 + data.limit())
    buffer.put(data)
  }
}

object VendorData {
  def fromArray(data: Array[Byte]): VendorData = fromAttr(ByteBuffer.wrap(data))
  def fromAttr(data: ByteBuffer): VendorData = {
    val vendorId = data.getInt
    new VendorData(vendorId, data.slice())
  }
}
class VendorData(val vendorId: Int, val data: ByteBuffer) {
  def this(vendorId: Int, data: Array[Byte]) = this(vendorId, ByteBuffer.wrap(data))
  /**
   * Interpret the vendor data as attributes.
   * @return
   */
  def readSubAttrs(): List[Attribute] = Attribute.readAll(data)
}
object Packet {
  // This is the limit according to RFC2138.  Thus, the entire buffer need only be 4098 bytes (which is much shorter than the max UDP packet size).
  def checkPacketLength(length: Int) = if (length > 4096) sys error s"Radius packet is too large: $length"
  def read(data: Array[Byte]): Packet = {
    val buffer = ByteBuffer.wrap(data)
    buffer.order(ByteOrder.BIG_ENDIAN) // ensure network byte order
    // Here we peek ahead to set the bounds on the buffer.
    val length: Int = buffer.getShort(2)
    buffer.limit(length - 1)
    read(buffer)
  }
  /**
   * Read a radius packet from a buffer.
   * @param buffer Whose current position must be at the start of the data.
   */
  def read(buffer: ByteBuffer): Packet = {
    buffer.order(ByteOrder.BIG_ENDIAN) // ensure network byte order
    val code = buffer.get()
    val id = buffer.get()
    val length = buffer.getShort
    buffer.limit(length - 1) // so we can catch errors
    checkPacketLength(length)
    val authenticator = buffer.getArray(16)
    // the rest will be the attributes
    val attributes = Attribute.readAll(buffer)
    // just checking!
    if (length != buffer.position) sys error s"Incorrect read of packet: length=$length, position=${buffer.position}"
    new Packet(code, id, authenticator, attributes.reverse)
  }
}

class Packet(val code: Byte , val packetId: Byte, val authenticator: ByteBuffer, val attributes: Seq[Attribute]) {
  /**
   * Useful for constructing packets for sending.
   */
  def this(code: Byte, packetId: Byte, authenticator: Array[Byte], attributes: Seq[Attribute]) =
    this(code, packetId, ByteBuffer.wrap(authenticator), attributes)
  val length = 20 + attributes.foldLeft(0) { (c, attr) => c + attr.length }
  /**
   * Write directly to an array.
   */
  def write(bytes: Array[Byte]): ByteBuffer = write(ByteBuffer.wrap(bytes))
  def write(buffer: ByteBuffer): ByteBuffer = {
    buffer.order(ByteOrder.BIG_ENDIAN) // ensure network byte order
    val length = 20 + attributes.foldLeft(0) { (c, attr) => c + attr.length }
    val b = buffer.slice()
    b.limit(length) // enforces that we write it correctly
    Packet.checkPacketLength(length)
    b.put(code)
    b.put(packetId)
    b.putShort(length)
    b.put(authenticator)
    attributes.foreach(_ write b)
    // just checking!
    if (length != b.position) sys error s"Incorrect write of packet: length=$length, position=${b.position}"
    buffer
  }
}
