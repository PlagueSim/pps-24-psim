package model.events
import model.core.SimulationState
import model.cure.Cure
import model.plague.Disease

/**
 *  A generic event buffer that needs to be extended to have a specified return type
 *  in [[modifyFunction]]
 * @param eventType default [[SimulationState]], needs to be specified between one of
 *                  its own parameters
 * @tparam A
 *   The type of result produced when the event is executed.
 */
private case class EventBuffer[A](eventType: SimulationState => A) extends Event[A]:
  private var eventList: List[Event[A]] = List.empty

  /**
   * adds an event to the queue
   * @param event the [[Event]] to add to the queue
   */
  def newEvent(event: Event[A]): Unit = this.synchronized:
    eventList = eventList :+ event

  /**
   * called by the engine to update the [[SimulationState]]
   */
  override def modifyFunction(state: SimulationState): A = this.synchronized:
    eventList.foreach(e => e.modifyFunction(state))
    eventList match
      case l if l.nonEmpty =>
        val event = l.last
        eventList = eventList.dropRight(1)
        execute()
        event.modifyFunction(state)
      case _ => eventType(state)

/**
 * The specific buffer only for [[Event]] that update [[Disease]]
 */
object DiseaseEventBuffer extends EventBuffer[Disease](_.disease)

/**
 * The specific buffer only for [[Event]] that update [[Cure]]
 */
object CureEventBuffer extends EventBuffer[Cure](_.cure)


