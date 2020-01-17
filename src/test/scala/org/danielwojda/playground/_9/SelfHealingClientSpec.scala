package org.danielwojda.playground._9

class SelfHealingClientSpec {

}

trait Client { //Great types, by the way
  def sendPing(): Unit
  def handlePong(handler: Unit => Unit): Unit
  def reconnect(): Unit
}