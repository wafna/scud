package wafna.udp.listener

import scala.annotation.tailrec

import java.net.{DatagramPacket, DatagramSocket}

import wafna.udp.util.UDPConf

/**
 * Receives messages and prints them to stdout.
 */
object Listener {
  def main(args: Array[String]): Unit = {
    val port: Int = UDPConf.port
    println(s"listening on port: $port")
    val socket = new DatagramSocket(port)
    val receiveBuffer = new Array[Byte](1024) // the limit is a tad less than 64K
    loop(socket, receiveBuffer)
  }
  @tailrec // this is super important or we'll blow out the stack, eventually.
  def loop(socket: DatagramSocket, receiveBuffer: Array[Byte]): Unit = {
    val receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length)
    socket.receive(receivePacket)
    // NB because we're reusing the buffer we need to be careful about how much data we scoop out of it.
    val receiveData = new String(receivePacket.getData, 0, receivePacket.getLength, "utf-8")
    val senderAddress = receivePacket.getAddress
    val senderPort = receivePacket.getPort
    println(s"from: $senderAddress:$senderPort, message: $receiveData")
    loop(socket, receiveBuffer)
  }
}
