package jtop.jmx

import scala.scalajs.js

@js.native
trait Client extends js.Object {

  def connect(): Unit
  def disconnect(): Unit

  def getAttribute(mbean: String, attribute: String, callback: js.Function): Unit

  def on(event: String, callback: js.Function): Unit

}
