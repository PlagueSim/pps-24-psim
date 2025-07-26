package model.events.plague

import model.core.SimulationState
import model.cure.CureModifier.{Additive, ProgressModifier}
import model.cure.*
import model.events.{CureEventBuffer, Event}
import model.plague.{Disease, Trait}
import model.world.Node

import scala.util.Random

object DiseaseEvents:

  /**
   * Checks if the [[Trait]] has elements that modify the cure in any way
   *
   * @param tr The [[Trait]] that is checked to eventually trigger
   *           events for [[Cure]] modification
   */
  private def triggerCureEvents(tr: Trait): Unit =
    if tr.stats.cureSlowdown != 0 then CureEventBuffer.newEvent(CureSlowDown(tr))
    if tr.stats.cureReset != 0 then CureEventBuffer.newEvent(CurePushBack(tr))

  /**
   * The [[Event]] used to evolve a new [[Trait]] in the [[Disease]]
   *
   * @param traitToEvolve the [[Trait]] that needs to be evolved
   */
  case class Evolution(traitToEvolve: Trait) extends Event[Disease]:
    triggerCureEvents(traitToEvolve)

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] with the evolved [[Trait]] if possible,
     *         the old instance otherwise
     */
    override def modifyFunction(state: SimulationState): Disease =
      state.disease.evolve(traitToEvolve) match
        case Left(str) => state.disease
        case Right(disease) => disease

  /**
   * The [[Event]] used to remove a [[Trait]] from the [[Disease]]
   *
   * @param traitToRemove the [[Trait]] that needs to be removed
   */
  case class Involution(traitToRemove: Trait) extends Event[Disease]:
    if traitToRemove.stats.cureSlowdown != 0 || traitToRemove.stats.cureReset != 0 then
      CureEventBuffer.newEvent(RemoveCureModifier(traitToRemove))

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] without the specified [[Trait]] if possible,
     *         the old instance otherwise
     */
    override def modifyFunction(state: SimulationState): Disease =
      state.disease.involve(traitToRemove) match
        case Left(str) => state.disease
        case Right(disease) => disease

  /**
   * The [[Event]] used to compute the amount of dna points to give to the [[Disease]]
   * depending on how many people where infected or deceased in a tick
   *
   * @param nodes the new state of the [[World]]
   */
  case class DnaPointsAddition(nodes: Map[String, Node]) extends Event[Disease]:

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] with incremented dna points
     */
    override def modifyFunction(state: SimulationState): Disease =
      val prevNodes = state.world.nodes
      val currentNodes = nodes

      val pointsToAdd: Int = DnaPointsCalculator.calculate(prevNodes, currentNodes)

      state.disease.addDnaPoints(pointsToAdd)

  /**
   * The [[Event]] used to check and if the [[Disease]] will randomly evolve a new [[Trait]]
   *
   * @param rng A function providing a random [[Double]]. Default [[Random.nextDouble]]
   */
  case class Mutation(rng: () => Double = () => Random.nextDouble()) extends Event[Disease]:
    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] with a randomly evolved [[Symptom]]
     *         or the old one
     */
    override def modifyFunction(state: SimulationState): Disease =
      if state.disease.mutationChance >= rng() then
        val (maybeTr, disease) = state.disease.randomMutation()
        maybeTr.foreach(triggerCureEvents)
        disease
      else state.disease


  /**
   * The [[Event]] used to slow down the [[Cure]] speed
   *
   * @param tr the [[Trait]] that slows down the [[Cure]]
   */
  case class CureSlowDown(tr: Trait) extends Event[Cure]:
    private val mod = CureModifier.additive(ModifierId(ModifierSource.Mutation(MutationId(tr.name)) ,ModifierKind.Additive), -tr.stats.cureSlowdown).get

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Cure]] with its speed updated
     */
    override def modifyFunction(state: SimulationState): Cure =
      state.cure.addModifier(mod)

  /**
   * The [[Event]] used to reduce the [[Cure]] progress
   *
   * @param tr the [[Trait]] that pushes the [[Cure]] progress back
   */
  case class CurePushBack(tr: Trait) extends Event[Cure]:
    val mod: OneTimeModifier =
      CureModifier.progressModifier(ModifierId(ModifierSource.Mutation(MutationId(tr.name)), ModifierKind.Additive), -tr.stats.cureReset).get

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Cure]] with its progress updated
     */
    override def modifyFunction(state: SimulationState): Cure =
      state.cure.addModifier(mod)

  /**
   * The [[Event]] used to remove the [[Cure]] modifiers applied
   * by a trait that is now being involved
   *
   * @param tr the [[Trait]] that is being involved
   */
  case class RemoveCureModifier(tr: Trait) extends Event[Cure]:
    val source: ModifierSource.Mutation = ModifierSource.Mutation(MutationId(tr.name))
    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Cure]] with its speed updated
     */
    override def modifyFunction(state: SimulationState): Cure =
      state.cure.removeModifiersBySource(source)