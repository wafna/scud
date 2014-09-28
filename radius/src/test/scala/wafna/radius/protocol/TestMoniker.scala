package wafna.radius.protocol

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

object MonikerA extends MonikerClass[MonikerA] {
  def apply(code: Byte, name: String) = instance(new MonikerA(code, name))
  val A1 = MonikerA(1, "1")
  val A2 = MonikerA(2, "2")
  val A3 = MonikerA(3, "3")
}
class MonikerA(code: Byte, name: String) extends Moniker(code, name)
object MonikerB extends MonikerClass[MonikerB] {
  def apply(code: Byte, name: String) = instance(new MonikerB(code, name))
  val B1 = MonikerB(1, "1")
  val B2 = MonikerB(2, "2")
  val B3 = MonikerB(3, "3")
  val B4 = MonikerB(3, "4")
}
class MonikerB(code: Byte, name: String) extends Moniker(code, name)

@RunWith(classOf[JUnitRunner])
class TestMoniker extends FunSuite {
  test("MonikerA") {
    def testNameAndCode(code: Byte, name: String): Unit = {
      assertResult(code, name)(MonikerA.byName(name).getOrElse(sys error s"bomb").code)
      assertResult(name, code)(MonikerA.byCode(code).getOrElse(sys error s"bomb").name)
    }
    assertResult(3, "size of A by code")(MonikerA.iterateByCode().toArray.size)
    assertResult(3, "size of A by value")(MonikerA.iterateByCode().toArray.size)
    (1 to 3) foreach { i => testNameAndCode(i.toByte, i.toString) }
    intercept[RuntimeException](testNameAndCode(86, "NoSuchElement"))
    intercept[RuntimeException](MonikerB(1, "Some other name"))
    intercept[RuntimeException](MonikerB(86, "1"))
  }
}

