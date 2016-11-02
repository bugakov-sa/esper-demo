import java.nio.file.Paths
import java.util

import com.espertech.esper.client._
import com.typesafe.scalalogging.Logger

import scala.beans.BeanProperty
import scala.io.Source

case class Event(@BeanProperty time: Long, @BeanProperty id: Long, @BeanProperty args: java.util.Map[String, java.lang.Object])

case class Schedule(@BeanProperty start: Long, @BeanProperty end: Long, @BeanProperty name: String)

object Application extends App {

  def event(id: Long, time: Long, args: Map[String, AnyRef]) = {
    val res = Event(time, id, new util.HashMap[String, java.lang.Object]())
    for ((k, v) <- args) {
      res.args.put(k, v.asInstanceOf[java.lang.Object])
    }
    res
  }

  val zone1 = 1.asInstanceOf[AnyRef]
  val on = 1.asInstanceOf[AnyRef]
  val off = 0.asInstanceOf[AnyRef]

  val events = Seq(
    event(1, 100, Map("zone" -> zone1, "alarm" -> on)),
    event(1, 102, Map("zone" -> zone1, "alarm" -> off)),
    event(1, 110, Map("zone" -> zone1, "alarm" -> on)),
    event(1, 123, Map("zone" -> zone1, "alarm" -> off))
  )

  val schedule = Seq(
    Schedule(0, 100, "Ivanov I.I."),
    Schedule(100, 200, "Petrov P.P.")
  )

  val log = Logger("Application")
  log.info("Started")

  val conf = new Configuration
  conf.addEventType("Event", classOf[Event])

  val engine = EPServiceProviderManager.getProvider("demo", conf)

  val stmts = Paths.get(System.getProperty("user.dir"), "conf").toFile.
    listFiles.
    filter(_.getName.startsWith("rule")).
    sortBy(_.getName).
    map(Source.fromFile(_).mkString).
    map(engine.getEPAdministrator.createEPL(_))

  stmts.last.addListenerWithReplay(new UpdateListener {
    override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) = {
      newEvents.foreach(e => log.info("{}", e.getUnderlying))
    }
  })

  events.foreach(engine.getEPRuntime.sendEvent(_))

  log.info("Terminated")
}