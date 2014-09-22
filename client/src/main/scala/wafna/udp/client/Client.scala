package wafna.udp.client

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import wafna.udp.util.UDPConf

/**
 * Sends messages and receives replies.
 */
object Client {
  def main(args: Array[String]): Unit = {
    val host = InetAddress.getByName("localhost")
    val port: Int = UDPConf.port
    val socket = new DatagramSocket()
    val receiveBuffer = new Array[Byte](1024) // the limit is a tad less than 64K
    def sendMessage(message: String): Unit = {
      println(s"sending: $message")
      val buffer = message.getBytes("utf-8")
      val sendPacket = new DatagramPacket(buffer, buffer.length, host, port)
      socket.send(sendPacket)
      val receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length)
      socket.receive(receivePacket)
      val reply = new String(receivePacket.getData, 0, receivePacket.getLength, "utf-8")
      println(s"received: $reply")
    }
    try {
      sendMessage("this is my message, there are many like it but this one is mine")
      sendMessage("don't fear the monad")
    } finally {
      socket.close()
    }
    println("client finished")
  }
}
