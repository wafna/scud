package wafna.radius

import wafna.udp.util.{ReadBuffer, WriteBuffer}

object PacketType {
  private var _byName = Map[String, PacketType]()
  private var _byCode = Map[Byte, PacketType]()

  def byName(name: String) = _byName get name

  def byCode(code: Byte) = _byCode get code

  def apply(code: Byte, name: String) = new PacketType(code, name)

  val AccessRequest = PacketType(1, "Access-Request")
  val AccessAccept = PacketType(2, "Access-Accept")
  val AccessReject = PacketType(3, "Access-Reject")
  val AccountingRequest = PacketType(4, "Accounting-Request")
  val AccountingResponse = PacketType(5, "Accounting-Response")
  val AccessChallenge = PacketType(11, "Access-Challenge")
  val StatusServer = PacketType(12, "Status-Server")
  val StatusClient = PacketType(13, "Status-Client")
}

class PacketType(val code: Byte, val name: String) {

  import wafna.radius.PacketType._

  if (255 == code) sys error "Illegal use of reserved packet type code 255"
  if (_byName contains name) sys error s"std attr $name already defined [${_byName.get(name).get}]"
  _byName += (name -> this)
  if (_byCode contains code) sys error s"std attr $code already defined [${_byCode.get(code).get}]"
  _byCode += (code -> this)
}

abstract class BaseAttrType(val code: Byte, val name: String)

/**
 * Dictionary of standard attribute types.
 */
object StdAttrType {
  private var _byName = Map[String, StdAttrType]()
  private var _byCode = Map[Byte, StdAttrType]()

  def byName(name: String) = _byName get name

  def byCode(code: Byte) = _byCode get code

  def apply(code: Byte, name: String) = new StdAttrType(code, name)

  val UserName = StdAttrType(1, "User-Name")
  val UserPassword = StdAttrType(2, "User-Password")
  val CHAPPassword = StdAttrType(3, " CHAP-Password")
  val NASIPAddress = StdAttrType(4, "NAS-IP-Address")
  val NASPort = StdAttrType(5, "NAS-Port")
  val ServiceType = StdAttrType(6, "Service-Type")
  val FramedProtocol = StdAttrType(7, "Framed-Protocol")
  val FramedIPAddress = StdAttrType(8, "Framed-IP-Address")
  val FramedIPNetmask = StdAttrType(9, "Framed-IP-Netmask")
  val FramedRouting = StdAttrType(10, "Framed-Routing")
  val FilterID = StdAttrType(11, "Filter-ID")
  val FramedMTU = StdAttrType(12, "Framed-MTU")
  val FramedCompression = StdAttrType(13, "Framed-Compression")
  val ReplyMessage = StdAttrType(19, "Reply-Message")
  val State = StdAttrType(24, "State")
  val Class = StdAttrType(25, "Class")
  val VendorSpecific = StdAttrType(26, "Vendor-Specific")
  val SessionTimeout = StdAttrType(27, "Session-Timeout")
  val IdleTimeout = StdAttrType(28, "Idle-Timeout")
  val TerminationAction = StdAttrType(29, "Termination-Action")
  val NASIdentifier = StdAttrType(32, "NAS-Identifier")
  val CalledStationId = StdAttrType(30, "Called-Station-Id")
  val CallingStationId = StdAttrType(31, "Calling-Station-Id")
  val ProxyState = StdAttrType(33, "Proxy-State")
  val LoginLATService = StdAttrType(34, "Login-LAT-Service")
  val LoginLATNode = StdAttrType(35, "Login-LAT-Node")
  val LoginLATGroup = StdAttrType(36, "Login-LAT-Group")
  val FramedAppleTalkLink = StdAttrType(37, "Framed-AppleTalk-Link")
  val FramedAppleTalkNetwork = StdAttrType(38, "Framed-AppleTalk-Network")
  val FramedAppleTalkZone = StdAttrType(39, "Framed-AppleTalk-Zone")
  // 40-59 reserved for accounting
  val CHAPChallenge = StdAttrType(60, "CHAP-Challenge")
  val NASPortType = StdAttrType(61, "NAS-Port-Type")
  val PortLimit = StdAttrType(62, "Port-Limit")
  val LoginLATPort = StdAttrType(63, "Login-LAT-Port")
}

class StdAttrType(code: Byte, name: String) extends BaseAttrType(code, name) {

  import wafna.radius.StdAttrType._

  if (_byName contains name) sys error s"std attr $name already defined [${_byName.get(name).get}]"
  _byName += (name -> this)
  if (_byCode contains code) sys error s"std attr $code already defined [${_byCode.get(code).get}]"
  _byCode += (code -> this)
}

/**
 * Vendor attribute types have no built in dictionary.  Users may make their own.
 */
class VendorAttrType(code: Byte, name: String) extends BaseAttrType(code, name)

//object Attribute {
//  def read(buffer: ReadBuffer): Attribute = {
//    val code = buffer.readByte()
//    val length = buffer.readByte()
//    val data = buffer.readBytes(length - 2)
//    new Attribute(code, data)
//  }
//}
//class Attribute(val code: Byte, val data: Array[Byte]) {
//  def write(buffer: WriteBuffer): Unit = {
//    buffer.writeByte(code)
//    buffer.writeByte(2 + data.length)
//    buffer.writeBytes(data)
//  }
//}

object BaseAttr {
  def read(buffer: ReadBuffer): BaseAttr = {
    val code = buffer.readByte()
    val length = buffer.readByte()
    val data = buffer.readBytes(length - 2)
    new BaseAttr(code, data)
  }
}
class BaseAttr(val code: Byte, val data: Array[Byte]) {
  if (data.length > Byte.MaxValue) sys error s"value is too large: ${data.length}, max is ${Byte.MaxValue}"
  def this(code: Byte, data: String, encoding: String = "utf-8") = this(code, data.getBytes(encoding))
  /**
   * Writes the attribute to a buffer and returns the number of bytes written (which will always be two plus the length
   * of the attribute value.
   * @param buffer byte array to which to write the attribute
   * @return the number of bytes written.
   */
  def write(buffer: WriteBuffer): Unit = {
    buffer.writeByte(code)
    buffer.writeByte(2 + data.length)
    buffer.writeBytes(data)
  }
}
class StdAttr(val attrType: StdAttrType, data: Array[Byte]) extends BaseAttr(attrType.code, data) {
  /**
   * Convenient for directly using strings to set attribute values.
   */
  def this(attrType: StdAttrType, data: String, encoding: String = "utf-8") = this(attrType, data getBytes encoding)

  /**
   * For readers
   */
  def this(attrType: Byte, data: Array[Byte]) = this(StdAttrType.byCode(attrType).getOrElse(sys error s"Unknown attribute type: $attrType"), data)
}
class VendorAttr(val attrType: Byte, data: Array[Byte]) extends BaseAttr(attrType, data) {
  /**
   * Convenient for directly using strings to set attribute values.
   */
  def this(attrType: Byte, data: String, encoding: String = "utf-8") = this(attrType, data getBytes encoding)
}
object Vendor {
  private var _byName = Map[String, Vendor]()
  private var _byCode = Map[Int, Vendor]()

  def byName(name: String) = _byName get name

  def byCode(code: Int) = _byCode get code

  def apply(code: Int, name: String) = new Vendor(code, name)

  val AcmeWidgets = Vendor(42, "Acme-Widgets")
}

/**
 * Represents the code and name of a radius client vendor, e.g. Nomadix, Zyxel, or Ruckus.
 */
class Vendor(val code: Int, val name: String) {

  import wafna.radius.Vendor._

  // The highest order byte must be zero so this is the maximum value.
  if (code > 256 * 256 * 256) sys error s"Illegal vendor code: too large: $code"
  if (_byName contains name) sys error s"std attr $name already defined [${_byName.get(name).get}]"
  _byName += (name -> this)
  if (_byCode contains code) sys error s"std attr $code already defined [${_byCode.get(code).get}]"
  _byCode += (code -> this)
}

object VendorSpecific {
  def read(buffer: ReadBuffer): VendorSpecific = {
    val attrType = buffer.readByte()
    val length = buffer.readByte()
    val vendorCode = buffer.readInt()
    val data = buffer.readBytes(length - 6) // account for vendor code as well as the other stuff
    new VendorSpecific(vendorCode, data)
  }
}

class VendorSpecific (vendor: Vendor, data: Array[Byte]) extends StdAttr(StdAttrType.VendorSpecific.code, data) {
  if (1 > data.length || 255 < data.length) sys error s"Invalid data size ${data.length}"
  def this(vendorCode: Int, data: Array[Byte]) = this(Vendor.byCode(vendorCode), data)
  /**
   * If the vendor structures its attributes this will parse them out.  Otherwise it will likely explode and show the
   * error.
   */
  lazy val subAttrs: Either[String, List[VendorAttr]] = try {
    val length = data.length
    val rb = new ReadBuffer(data, 0)
    var attributes: List[VendorAttr] = Nil
    while (rb.position < length) {
      val attrType = rb.readByte()
      val length = rb.readByte()
      val attrData = rb.readBytes(length - 2)
      attributes ::= new VendorAttr(attrType, attrData)
    }
    Right(attributes)
  } catch {
    case e: Throwable => Left(e.getMessage)
  }
  override def write(buffer: WriteBuffer): Unit = {
    buffer.writeByte(code)
    buffer.writeByte((6 + data.length).toByte)
    buffer.writeInt(vendor.code)
    buffer.writeBytes(data)
  }
}

object RadiusPacket {
  /**
   * Read a radius packet from a buffer.
   */
  def read(bytes: Array[Byte]): RadiusPacket = {
    val rb = new ReadBuffer(bytes, 0)
    val code = rb.readByte()
    val id = rb.readByte()
    val length = rb.readShort()
    val authenticator = rb.readBytes(16)
    // the rest will be the attributes
    var attributes: List[StdAttr] = Nil
    while (rb.position < length) {
      val attrCode = rb.readByte()

      attributes ::= (if (attrCode == StdAttrType.VendorSpecific.code) {
        val dataLength = rb.readByte()
        val data = rb.readBytes(dataLength)
        val attrType = (StdAttrType byCode attrCode).getOrElse(sys error s"No attribute type for code $attrCode")
        new StdAttr(attrType, data)
      } else {
        ???
      })
    }
    // If this trips we've surely run over or the while loop wouldn't have terminated normally.
    if (length!= rb.position) sys error s"Incorrect read of packet: expected $length but read ${rb.position}."
    val packetType: PacketType = (PacketType byCode code).getOrElse(sys error s"No packet type for code $code")
    // We reverse the attributes so they come out in the order in which they're represented in the packet.
    // It's not clear this is necessary but it at least makes testing simpler.
    new RadiusPacket(packetType, id, authenticator, attributes.reverse)
  }
}

class RadiusPacket(val packetType: PacketType, val packetId: Byte, val authenticator: Array[Byte], val attributes: Seq[StdAttr]) {
  if (16 != authenticator.length) sys error s"Invalid authenticator length: required 16 bytes, got ${authenticator.length}"

  /**
   * Write a radius packet to a buffer.
   */
  def write(buffer: Array[Byte]): Int = {
    val wb = new WriteBuffer(buffer, 0)
    val length = 20 + attributes.foldLeft(0) { (c, attr) => c + 2 + attr.data.length }
    // This is the limit according to RFC2138.  Thus, the entire buffer need only be 4098 bytes (which is much shorter than the max UDP packet size).
    if (length > 4096) sys error s"Radius packet is too large: $length"
    wb.writeByte(packetType.code)
    wb.writeByte(packetId)
    wb.writeShort(length)
    wb.writeBytes(authenticator)
    attributes.foreach(_ write wb)
    val pos = wb.position
    // just checking! account for code and id
    if (pos != length) sys error s"Somethings off: $pos $length"
    pos
  }
}
