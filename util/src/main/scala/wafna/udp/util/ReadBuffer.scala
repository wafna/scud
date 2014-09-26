package wafna.udp.util

import java.nio.ByteBuffer

class ReadBuffer(buffer: Array[Byte], at: Int) {
  private var nth = at
  def position = nth
  def readByte(): Byte = {
    val b = buffer(nth)
    nth += 1
    b
  }
  def readShort(): Short = {
    val v = ByteBuffer.wrap(buffer, nth, 2).getShort
    nth += 2
    v
  }
  def readInt(): Int = {
    val v = ByteBuffer.wrap(buffer, nth, 4).getInt
    nth += 4
    v
  }
  def readBytes(count: Int): Array[Byte] = {
    val bytes = new Array[Byte](count)
    for (i <- 0 until count) bytes(i) = readByte()
    bytes
  }
}
