package model.events.cure

import model.core.SimulationState
import model.cure.{Cure, CureModifiers, ModifierId, ModifierKind, ModifierSource, NodeId}
import model.cure.CureModifiers.CureModifiersBuilder
import model.cure.ModifierSource.Node
import model.events.Event
import model.plague.Disease
import model.world.World

case object GlobalCureResearchEvent extends Event[Cure]:

  // Soglie configurabili
  private val SEVERITY_THRESHOLD  = 20
  private val INFECTION_THRESHOLD = 0.6

  override def modifyFunction(state: SimulationState): Cure =
    if state.disease.severity >= SEVERITY_THRESHOLD then
      val world   = state.world
      val disease = state.disease

      // Calcola il contributo di ogni nodo alla ricerca della cura globale
      val contributions = world.nodes.keys.map:
        nodeId =>
        nodeId -> calculateCureContribution(nodeId, world)
      .toMap

      val builder = CureModifiers.builder
      contributions.foreach:
        case (nodeId, contribution) =>
        builder.addAdditive(
          ModifierId(Node(NodeId(nodeId)), ModifierKind.Additive),
          contribution
        )
      val newModifiers = state.cure.modifiers.modifiers ++ builder.build.modifiers
      
      Cure.builder
        .withProgress(state.cure.progress)
        .withBaseSpeed(state.cure.baseSpeed)
        .withModifiers(CureModifiers(newModifiers))
        .build

    state.cure

  private def totalPopulation(world: World): Double =
    world.nodes.values.map(_.population).sum

  private def nodeInfectedRatio(nodeId: String, world: World): Double =
    world.nodes.get(nodeId) match
      case Some(node) => node.infected / node.population.toDouble
      case None       => 0.0

  /** Calculates the contribution of a node to the global cure research based on
    * its infected population and the total population of the world.
    *
    * @param nodeId
    *   The identifier of the node.
    * @param world
    *   The current state of the world.
    * @return
    *   The contribution of the node to the global cure research.
    */
  private def calculateCureContribution(nodeId: String, world: World): Double =
    world.nodes.get(nodeId) match
      case Some(node) => node.infected / totalPopulation(world)
      case None       => 0.0
