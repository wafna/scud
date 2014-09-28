package wafna.radius.protocol

/**
 * Mappings of the monikers in a class by name and code.
 * This trait is to be mixed into the companion object for the class deriving from Moniker, i.e. the class defining the
 * new type of moniker.
 * @tparam T The type of the moniker in the class.
 */
trait MonikerClass[T <: Moniker] {
  private var _byName = Map[String, T]()
  private var _byCode = Map[Byte, T]()

  def byName(name: String) = _byName get name

  def byCode(code: Byte) = _byCode get code
  protected def instance(moniker: T): T = {
    if (_byName contains moniker.name) sys error s"std attr ${moniker.name} already defined [${_byName.get(moniker.name).get}]"
    _byName += (moniker.name -> moniker)
    if (_byCode contains moniker.code) sys error s"std attr ${moniker.code} already defined [${_byCode.get(moniker.code).get}]"
    _byCode += (moniker.code -> moniker)
    moniker
  }
  def iterateByCode(): Iterable[T] = _byCode.values
  def iterateByValue(): Iterable[T] = _byCode.values
}
/**
 * Each moniker is a set of pairs of unique codes and unique names.
 * To make a new moniker type, extend this class and extend it's companion object with the MonikerClass trait.
 * Put new instances of this type in the companion object and wrap their creation with the instance method.
 * The easiest way to do this is to have the moniker's ctor called from the object's apply method.
 */
class Moniker(val code: Byte, val name: String)
