package model.events.cure

import model.core.SimulationState
import model.cure.{
  Cure,
  CureModifiers,
  ModifierId,
  ModifierKind,
  ModifierSource,
  NodeId
}
import model.cure.CureModifiers.CureModifiersBuilder
import model.cure.ModifierSource.Node
import model.events.Event
import model.plague.Disease
import model.world.World

case object GlobalCureResearchEvent extends Event[Cure]:

  private val SEVERITY_THRESHOLD  = 20
  private val INFECTION_THRESHOLD = 0.6

  override def modifyFunction(state: SimulationState): Cure =
    if state.disease.severity >= SEVERITY_THRESHOLD then
      val world   = state.world
      val disease = state.disease

      val contributions = world.nodes.keys
        .map: nodeId =>
          nodeId -> calculateCureContribution(nodeId, world)
        .toMap

      val newModifiers =
        state.cure.modifiers.modifiers ++ contributionsToAdditive(
          contributions
        ).modifiers

      Cure.builder
        .withProgress(state.cure.progress)
        .withBaseSpeed(state.cure.baseSpeed)
        .withModifiers(CureModifiers(newModifiers))
        .build

    state.cure

  private def contributionsToAdditive(contributions: Map[String, Double]): CureModifiers =
    contributions.foldLeft(CureModifiers.builder):
      case (builder, (nodeId, contribution)) =>
      builder.addAdditive(ModifierId(Node(NodeId(nodeId)), ModifierKind.Additive), contribution)
    .build

  private def totalPopulation(world: World): Double =
    world.nodes.values.map(_.population).sum

  private def nodeInfectedRatio(nodeId: String, world: World): Double =
    world.nodes.get(nodeId).map(node => node.infected / node.population.toDouble).getOrElse(0.0)

  private def calculateCureContribution(nodeId: String, world: World): Double =
    world.nodes.get(nodeId).map(node => node.infected / totalPopulation(world)).getOrElse(0.0)
