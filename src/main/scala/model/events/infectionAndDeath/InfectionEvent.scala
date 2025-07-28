package model.events.infectionAndDeath

import model.core.SimulationState
import model.events.Event
import model.world.Node

/** The infection event */
case class InfectionEvent() extends Event[Map[String, Node]]:
  override def modifyFunction(state: SimulationState): Map[String, Node] =
    state.world.nodes.foldLeft(Map.empty[String, Node]):
      (acc, nodeEntry) =>
      val (nodeId, node) = nodeEntry
      val infectedNode   =
        state.infectionLogic.applyToPopulation(node, state.disease)
      acc + (nodeId -> infectedNode)

/** The Death event */
case class DeathEvent() extends Event[Map[String, Node]]:
  override def modifyFunction(state: SimulationState): Map[String, Node] =
    state.world.nodes.foldLeft(Map.empty[String, Node]):
      (acc, nodeEntry) =>
      val (nodeId, node) = nodeEntry
      val deadNode = state.deathLogic.applyToPopulation(node, state.disease)
      acc + (nodeId -> deadNode)
