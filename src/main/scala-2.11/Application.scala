import java.nio.file.Paths

import com.espertech.esper.client._
import com.typesafe.scalalogging.Logger

import scala.beans.BeanProperty
import scala.collection.immutable.IndexedSeq
import scala.io.Source

case class Alert(@BeanProperty val time: Long, @BeanProperty zone: Long, @BeanProperty val isTrue: Boolean)

object Application extends App {
  val log = Logger("Application")
  log.info("Started")

  val conf = new Configuration
  conf.addEventType("AlertEvent", classOf[Alert])

  val engine = EPServiceProviderManager.getProvider("demo", conf)

  val n = 2

  def ruleText(i: Int) = Source.fromFile(Paths.get(System.getProperty("user.dir"), "conf", "rule" + i).toFile).mkString

  val stmts = for (i <- 1 to n) yield engine.getEPAdministrator.createEPL(ruleText(i))

  stmts.last.addListenerWithReplay(new UpdateListener {
    override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) = {
      newEvents.foreach(e => log.info("{}", e.getUnderlying))
    }
  })

  engine.getEPRuntime.sendEvent(Alert(0, 1, true))
  engine.getEPRuntime.sendEvent(Alert(1, 1, false))
  engine.getEPRuntime.sendEvent(Alert(4, 1, true))
  engine.getEPRuntime.sendEvent(Alert(9, 1, false))

  log.info("Terminated")
}
