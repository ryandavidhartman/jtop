package jtop

import scala.scalajs.js

object Implicits {
  import upickle.default._
  import scala.language.implicitConversions

  implicit def toJsAny[T : Writer](expr: T): js.Any =
    writeJs(writeJs(expr)).asInstanceOf[js.Any]
}
