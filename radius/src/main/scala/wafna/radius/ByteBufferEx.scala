package wafna.radius

import java.nio.ByteBuffer
import scala.language.implicitConversions

object ByteBufferEx {
  implicit def enhanceByteBuffer(buffer: ByteBuffer) = new ByteBufferEx(buffer)
}
/**
 * Enhanced ByteBuffer
 */
class ByteBufferEx(buffer: ByteBuffer) {
  /**
   * Create a new buffer holding the requested bytes. This buffer is only a slice of the source buffer. This saves memory.
   * The source buffer's position is incremented by the count.
   */
  def getArray(count: Int): ByteBuffer = {
    val slice = buffer.slice()
    slice.limit(count - 1)
    buffer.position(count + buffer.position)
    slice
  }
  def copy(): Array[Byte] = buffer.array() // todo this may actually return the original array and not the restricted view we require.
  def putShort(i: Int): ByteBuffer = {
    if (i > Short.MaxValue || i < Short.MinValue) sys error s"Invalid short value: $i"
    buffer.putShort(i.toShort)
  }
  def putIntAsByte(i: Int): ByteBuffer = {
    if (i > Byte.MaxValue || i < Byte.MinValue) sys error s"Invalid byte value: $i"
    buffer.put(i.toByte)
  }
}
