package model.events

import model.core.SimulationState
import model.world.Node

case class InfectionEvent() extends Event[Map[String, Node]]:
  override def modifyFunction(state: SimulationState): Map[String, Node] = 
    state.world.nodes.foldLeft(Map.empty[String, Node]) { (acc, nodeEntry) =>
      val (nodeId, node) = nodeEntry
      val infectedNode = state.infectionLogic.applyToPopulation(node, state.disease)
      acc + (nodeId -> infectedNode)
    }
