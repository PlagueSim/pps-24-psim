package view.event

import model.world.EdgeType
import model.world.{Node => ModelNode}

sealed trait ViewEvent

case object StartWorldSimulation extends ViewEvent
case object PauseWorldSimulation extends ViewEvent

case class MovePeople(from: String, to: String, amount: Int) extends ViewEvent

case class AddEdge(from: String, to: String, typology: EdgeType) extends ViewEvent
case class RemoveEdge(from: String, to: String, typology: EdgeType) extends ViewEvent

case class AddNode(id: String, data: ModelNode) extends ViewEvent
case class RemoveNode(id: String) extends ViewEvent
case class UpdateNodePosition(id: String, position: (Double, Double)) extends ViewEvent
case class UpdateNodeLabels(id: String, labels: Map[String, String]) extends ViewEvent
