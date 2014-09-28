package wafna.radius.protocol

object StdAttrType extends MonikerClass[StdAttrType] {
  def apply(code: Byte, name: String) = {
    val StdAttrType = new StdAttrType(code, name)
    instance(StdAttrType)
    StdAttrType
  }
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

class StdAttrType(code: Byte, name: String) extends Moniker(code, name)
