package packb

import packa._

class Foo {
  
  def bar {
    import Messages._
    
    foo(MsgA("a"))
    foo(MsgB)
  }
  
  def foo(x: Any) = {
    x
  }
  
}