package model.events
import model.core.SimulationState
import model.plague.Disease

case object EventBuffer extends Event[Disease]:
  private var eventList: List[Event[Disease]] = List.empty

  def newEvent(event: Event[Disease]): Unit = this.synchronized:
    eventList = eventList.appended(event)

  override def modifyFunction(state: SimulationState): Disease = this.synchronized:
    eventList.foreach(e => e.modifyFunction(state))
    eventList match
      case l if l.nonEmpty =>
        val event = l.last
        eventList = eventList.dropRight(1)
        execute()
        event.modifyFunction(state)
      case _ => state.disease
