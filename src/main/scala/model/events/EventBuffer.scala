package model.events
import model.core.SimulationState
import model.cure.Cure
import model.plague.Disease

/**
 *
 * @param eventType
 * @tparam A
 *   The type of result produced when the event is executed.
 */
private case class EventBuffer[A](eventType: SimulationState => A) extends Event[A]:
  private var eventList: List[Event[A]] = List.empty

  def newEvent(event: Event[A]): Unit = this.synchronized:
    eventList = eventList :+ event

  override def modifyFunction(state: SimulationState): A = this.synchronized:
    eventList.foreach(e => e.modifyFunction(state))
    eventList match
      case l if l.nonEmpty =>
        val event = l.last
        eventList = eventList.dropRight(1)
        execute()
        event.modifyFunction(state)
      case _ => eventType(state)

object DiseaseEventBuffer extends EventBuffer[Disease](_.disease)

object CureEventBuffer extends EventBuffer[Cure](_.cure)


