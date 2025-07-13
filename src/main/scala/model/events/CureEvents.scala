package model.events

import model.core.SimulationState
import model.core.SimulationEngine.Simulation
import model.cure.Cure
import cats.data.State

/** Event that only execute the advance of the cure progress.
  *
  * This event modifies the simulation state by advancing the cure's progress
  * based on the current day and applying any modifiers that may be present.
  */
case class BasicCureEvent() extends Event[Cure]:
  override def modifyFunction(state: SimulationState): Cure =
    state.cure.advance()

case class LinearInfectedThresholdEvent(threshold: Double = 0.5)
    extends Event[Cure]:
  override def modifyFunction(state: SimulationState): Cure =
    val nodeModifiers = state.world.nodes.collect:
      case (nodeId, node)
          if node.population > 0 && node.infected.toDouble / node.population > threshold =>
        val modId = model.cure.ModifierId(
          model.cure.ModifierSource.Node(model.cure.NodeId(nodeId)),
          model.cure.ModifierKind.Additive
        )
        modId -> model.cure.CureModifier.Additive(modId, 0.01)
        
    val missingModifiers = nodeModifiers.filterNot:
      case (modId, _) =>
        state.cure.modifiers.modifiers.contains(modId)

    missingModifiers.values.foldLeft(state.cure): (cure, mod) =>
      cure.copy(modifiers = cure.modifiers.add(mod))
