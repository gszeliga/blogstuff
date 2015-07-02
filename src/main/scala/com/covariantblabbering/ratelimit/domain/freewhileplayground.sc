import com.covariantblabbering.ratelimit.domain.FreeWhile.doWhile
import com.covariantblabbering.ratelimit.domain.Transform.{evaluator, Partial}
import com.covariantblabbering.ratelimit.domain.{TestMe, test, Transform, Ops}
import com.covariantblabbering.ratelimit.domain.Ops.{zero, MyOps}

import scalaz.Free

import Ops._

/*def count(i: Int):Free[MyOps,Int] = {

  def doCount(partial:Int):Free[Ops,Int] = {
    doWhile((counter:Int) => counter < i)(() => zero) {
      () => Ops.value(partial) flatMap (v => doCount(v + 1))
    }

  doCount(0)

}*/

/*val counter:Free[MyOps,Int] = count(2)*/

val counter = value(7) 

/*Free.runFC(counter)(Evaluator).exec(0)*/
/*Free.runFC(counter)(evaluator).exec(0)*/

test.main(Array("hola"))
TestMe.go()
