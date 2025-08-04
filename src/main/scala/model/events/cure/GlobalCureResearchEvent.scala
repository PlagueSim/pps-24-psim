package model.events.cure

import model.core.SimulationState
import model.cure.{Cure, CureModifier, CureModifiers, ModifierId, ModifierKind, ModifierSource, NodeId}
import model.cure.CureModifiers.CureModifiersBuilder
import model.cure.ModifierSource
import model.events.Event
import model.plague.Disease
import model.world.{Node, World}

/**
 * Event that applies global cure research based on disease severity and node infection ratios.
 *
 * If the disease severity exceeds a threshold, nodes with infected ratios above a threshold
 * contribute additive cure modifiers. Otherwise, the cure state remains unchanged.
 */
case class GlobalCureResearchEvent() extends Event[Cure]:

  private val SEVERITY_THRESHOLD  = 8
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
      val severity = state.disease.severity
      val baseModifiers = removeNodeModifiers(state.cure.modifiers.modifiers)
      val nodes = highlyInfectedNodes(world)
      val contributions = nodes.map(id => id -> cureContribution(id, world,severity)).toMap
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
  private def cureContribution(nodeId: String, world: World, severity: Double): Double = {
    val node = world.nodes(nodeId)
    val capacityFactor = totalNodePopulation(node) / totalPopulation(world)
    val infectionFactor = node.infected.toDouble / totalNodePopulation(node)
    val severityFactor = 1 + (severity - SEVERITY_THRESHOLD) * 0.01
    val rawContribution = (capacityFactor * infectionFactor * severityFactor * 0.2).min(1.0).max(0.0)
    println(s"Node $nodeId contributes ${rawContribution * 100}% to the cure.")
    rawContribution
  }

  /** Returns the total population of the world. population + deaths */
  private def totalPopulation(world: World): Double =
    world.nodes.values.map(totalNodePopulation).sum

  private def totalNodePopulation(node: Node): Double =
    node.population + node.died

  /** Removes all node-based additive modifiers from the current modifiers map. */
  private def removeNodeModifiers(modifiers: Map[ModifierId, CureModifier]): Map[ModifierId, CureModifier] =
    modifiers.filterNot { case (id, _) => id.source.isInstanceOf[ModifierSource.Node] && id.kind == ModifierKind.Additive }

  /** Builds a map of additive modifiers from node cure contributions. */
  private def buildAdditiveModifiers(contributions: Map[String, Double]): Map[ModifierId, CureModifier] =
    contributions.collect {
      case (nodeId, value) if value > 0.0 =>
        val modId = ModifierId(ModifierSource.Node(NodeId(nodeId)), ModifierKind.Additive)
        modId -> CureModifier.additive(modId, value).getOrElse(throw new IllegalArgumentException(s"Invalid modifier for node $nodeId"))
    }
