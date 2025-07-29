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
  /** Advances the cure progress based on the current day and modifiers.
    * @param state The current simulation state.
    * @return The updated cure state with advanced progress.
    */
  override def modifyFunction(state: SimulationState): Cure =
    state.cure.advance()

/**
 * Event that applies an additive cure modifier to nodes whose infected ratio exceeds a threshold.
 * @param threshold The minimum infected ratio required to apply the modifier (default: 0.5).
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

  /**
   * Applies additive cure modifiers to nodes exceeding the infected threshold.
   * @param state The current simulation state.
   * @return The updated cure state with new modifiers applied.
   */
  override def modifyFunction(state: SimulationState): Cure =
    missingModifiers(state).values.foldLeft(state.cure): (cure, mod) =>
      cure.addModifier(mod)

/**
 * Event that applies a progress modifier to the global cure state.
 * @param progress The progress value to subtract from the cure.
 */
case class ProgressSubtractExampleEvent(progress: Double) extends Event[Cure]:
  /**
   * Applies a progress modifier to the global cure state.
   * @param state The current simulation state.
   * @return The updated cure state with the progress modifier applied.
   */
  override def modifyFunction(state: SimulationState): Cure =
    val modId = model.cure.ModifierId(
      model.cure.ModifierSource.Global,
      model.cure.ModifierKind.ProgressModifier
    )
    val modifier = model.cure.CureModifier.progressModifier(modId, progress)
    modifier match
      case Some(mod) => state.cure.addModifier(mod)
      case None      => state.cure
