package wafna.udp.util

import java.nio.ByteBuffer

/**
 * Encapsulates a buffer with position for successive writes.
 */
class WriteBuffer(buffer: Array[Byte], at: Int) {
  private var nth = at
  def position = nth
  def writeByte(i: Byte): Int = {
    buffer(nth) = i
    nth += 1
    nth
  }
  def writeShort(i: Short): Int = writeBytes(ByteBuffer.allocate(2).putShort(i))
  def writeInt(i: Int): Int = writeBytes(ByteBuffer.allocate(4).putInt(i))
  def writeBytes(bytes: Array[Byte]): Int = {
    for (byte <- bytes) writeByte(byte)
    nth
  }
  def writeBytes(bb: ByteBuffer): Int = {
    // todo getting the array here is a inefficient.
    writeBytes(bb.array())
  }
}
