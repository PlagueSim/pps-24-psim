package model.events.cure

import model.core.SimulationState
import model.cure.{
  Cure,
  CureModifiers,
  ModifierId,
  ModifierKind,
  ModifierSource,
  NodeId,
  CureModifier
}
import model.cure.CureModifiers.CureModifiersBuilder
import model.cure.ModifierSource.Node
import model.events.Event
import model.plague.Disease
import model.world.World

/**
 * Event that applies global cure research based on disease severity and node infection ratios.
 *
 * If the disease severity exceeds a threshold, nodes with infected ratios above a threshold
 * contribute additive cure modifiers. Otherwise, the cure state remains unchanged.
 */
case object GlobalCureResearchEvent extends Event[Cure]:

  private val SEVERITY_THRESHOLD  = 20
  private val INFECTION_THRESHOLD = 0.6

  /**
   * Modifies the cure state by applying global research contributions from highly infected nodes.
   *
   * If the disease severity is above the threshold, nodes with infected ratios above the infection threshold
   * contribute to the cure via additive modifiers. Otherwise, returns the current cure state unchanged.
   *
   * @param state The current simulation state.
   * @return The updated cure state with new global research modifiers if applicable.
   */
  override def modifyFunction(state: SimulationState): Cure =
    if state.disease.severity < SEVERITY_THRESHOLD then state.cure
    else
      val world = state.world
      val baseModifiers = removeNodeModifiers(state.cure.modifiers.modifiers)
      val nodes = highlyInfectedNodes(world)
      val contributions = nodes.map(id => id -> cureContribution(id, world)).toMap
      val newModifiers = baseModifiers ++ buildAdditiveModifiers(contributions)
      Cure.builder
        .withProgress(state.cure.progress)
        .withBaseSpeed(state.cure.baseSpeed)
        .withModifiers(CureModifiers.builder.withModifiers(newModifiers).build)
        .build

  /** Returns the list of node IDs whose infected ratio exceeds the infection threshold. */
  private def highlyInfectedNodes(world: World): List[String] =
    world.nodes.collect {
      case (id, node) if node.population > 0 && node.infected.toDouble / node.population >= INFECTION_THRESHOLD => id
    }.toList

  /** Computes the cure contribution for a node as infected/population ratio over total population. */
  private def cureContribution(nodeId: String, world: World): Double =
    world.nodes.get(nodeId).map(_.infected.toDouble).getOrElse(0.0) / totalPopulation(world)

  /** Returns the total population of the world. */
  private def totalPopulation(world: World): Double =
    world.nodes.values.map(_.population).sum.toDouble

  /** Removes all node-based additive modifiers from the current modifiers map. */
  private def removeNodeModifiers(modifiers: Map[ModifierId, CureModifier]): Map[ModifierId, CureModifier] =
    modifiers.filterNot { case (id, _) => id.source.isInstanceOf[Node] && id.kind == ModifierKind.Additive }

  /** Builds a map of additive modifiers from node cure contributions. */
  private def buildAdditiveModifiers(contributions: Map[String, Double]): Map[ModifierId, CureModifier] =
    contributions.collect {
      case (nodeId, value) if value > 0.0 =>
        val modId = ModifierId(Node(NodeId(nodeId)), ModifierKind.Additive)
        modId -> CureModifier.additive(modId, value).getOrElse(throw new IllegalArgumentException(s"Invalid modifier for node $nodeId"))
    }
