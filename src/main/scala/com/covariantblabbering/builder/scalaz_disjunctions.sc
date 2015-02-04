import scalaz._
import std.option._, std.list._
import scalaz.syntax.either._

//http://eed3si9n.com/learning-scalaz/Either.html

1.right[String]
2.left[String]
1.right[String] map (_+2)