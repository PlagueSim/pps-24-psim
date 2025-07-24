package model.events.cure

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState
import model.cure.Cure
import model.events.Event

/** Event that only execute the advance of the cure progress.
  *
  * This event modifies the simulation state by advancing the cure's progress
  * based on the current day and applying any modifiers that may be present.
  */
case class AdvanceCureEvent() extends Event[Cure]:
  override def modifyFunction(state: SimulationState): Cure =
    state.cure.advance()

/** Event that advances the cure progress linearly based on infected nodes. Adds
  * a small additive modifier for each node above the infection threshold.
  * @param threshold
  *   The infection ratio above which a node contributes to cure progress.
  */
case class LinearInfectedThresholdEvent(threshold: Double = 0.5)
    extends Event[Cure]:

  private def nodeModifiers(state: SimulationState) =
    state.world.nodes.collect:
      case (nodeId, node)
          if node.population > 0 && node.infected.toDouble / node.population > threshold =>
        val modId = model.cure.ModifierId(
          model.cure.ModifierSource.Node(model.cure.NodeId(nodeId)),
          model.cure.ModifierKind.Additive
        )
        modId -> model.cure.CureModifier
          .additive(modId, 0.1)
          .getOrElse(
            throw new IllegalArgumentException(
              s"Invalid modifier for node $nodeId with threshold $threshold"
            )
          )

  private def missingModifiers(state: SimulationState) =
    nodeModifiers(state).filterNot:
      case (modId, _) =>
        state.cure.modifiers.modifiers.contains(modId)

  override def modifyFunction(state: SimulationState): Cure =
    missingModifiers(state).values.foldLeft(state.cure): (cure, mod) =>
      cure.addModifier(mod)

case class ProgressSubtractExampleEvent(progress: Double) extends Event[Cure]:
  override def modifyFunction(state: SimulationState): Cure =
    val modId = model.cure.ModifierId(
      model.cure.ModifierSource.Global,
      model.cure.ModifierKind.ProgressModifier
    )
    val modifier = model.cure.CureModifier.progressModifier(modId, progress)
    modifier match
      case Some(mod) => state.cure.addModifier(mod)
      case None      => state.cure
