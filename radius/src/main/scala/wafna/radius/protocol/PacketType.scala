package wafna.radius.protocol

object PacketType extends MonikerClass[PacketType] {
  def apply(code: Byte, name: String) = {
    val packetType = new PacketType(code, name)
    instance(packetType)
    packetType
  }
  val AccessRequest = PacketType(1, "Access-Request")
  val AccessAccept = PacketType(2, "Access-Accept")
  val AccessReject = PacketType(3, "Access-Reject")
  val AccountingRequest = PacketType(4, "Accounting-Request")
  val AccountingResponse = PacketType(5, "Accounting-Response")
  val AccessChallenge = PacketType(11, "Access-Challenge")
  val StatusServer = PacketType(12, "Status-Server")
  val StatusClient = PacketType(13, "Status-Client")
}

class PacketType(code: Byte, name: String) extends Moniker(code, name) {
  if (255 == code) sys error "Illegal use of reserved packet type code 255"
}
