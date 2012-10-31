package packa

object Messages {
  case class MsgA(str: String, str2: String = "abc")
  case class MsgB(str: String)
  case object MsgC
}