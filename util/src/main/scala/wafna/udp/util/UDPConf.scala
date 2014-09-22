package wafna.udp.util

import com.typesafe.config.{Config, ConfigFactory}

object UDPConf {
  protected def loadConfig(): Config = ConfigFactory.load(this.getClass.getClassLoader, "app.conf")
  private val config = loadConfig() // doesn't need to be lazy
  val port = config.getInt("port")
}
