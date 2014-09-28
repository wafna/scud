package wafna.radius.protocol

import java.nio.{ByteOrder, ByteBuffer}

/**
 * Demonstracting getting values in and out of a buffer..
 */
object DemoByteBuffer {
  def testAnswer(exp: Int, got: Int, msg: String): Unit = {
    println(s"$msg: exp=$exp, got=$got, ${exp == got}")
  }
  def main(args: Array[String]): Unit = {
    val buffer = ByteBuffer.allocate(100)
    buffer.order(ByteOrder.BIG_ENDIAN)
    val byte: Byte = 7
    val short: Short = 20
    val int: Int = 30000
    buffer.put(byte)
    buffer.putShort(short)
    buffer.putInt(int)
    buffer.position(0)
    testAnswer(byte, buffer.get(), "byte")
    testAnswer(short, buffer.getShort, "short")
    testAnswer(int, buffer.getInt, "int")
  }
}
