package wafna.udp.sender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}
import wafna.udp.util.UDPConf

/**
 * Only sends messages.  Intended for use with the Listener.
 */
object Sender {
  def main(args: Array[String]): Unit = {
    val host = InetAddress.getByName("localhost")
    val port: Int = UDPConf.port
    val socket = new DatagramSocket()
    def sendMessage(message: String): Unit = {
      println(s"sending: $message")
      val buffer = message.getBytes("utf-8")
      val packet = new DatagramPacket(buffer, buffer.length, host, port)
      socket.send(packet)
    }
    try {
      sendMessage("I'm outdoorsy in that I like getting drunk on patios.")
      sendMessage("You make me wish I had more middle fingers.")
    } finally {
      socket.close()
    }
    println("client finished")
  }
}
