package model.events
import model.core.SimulationState
import model.cure.{Cure, CureModifier, ModifierId, ModifierKind, ModifierSource, MutationId}
import model.cure.CureModifier.Additive
import model.plague.{Disease, Trait}
import model.world.Node

import scala.util.Random

object DiseaseEvents:

  /**
   * The [[Event]] used to evolve a new [[Trait]] in the [[Disease]]
   *
   * @param traitToEvolve the [[Trait]] that needs to be evolved
   */
  case class Evolution(traitToEvolve: Trait) extends Event[Disease]:
    if traitToEvolve.stats.cureSlowdown != 0 then CureEventBuffer.newEvent(CureSlowDown(traitToEvolve))

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
   * The [[Event]] used to increment the dna points of the [[Disease]]
   *
   * @param pointsToAdd the amount of points to add
   */
  case class DnaPointsAddition(nodes: Map[String, Node]) extends Event[Disease]:
    private val newNodeDnaMul = 5
    private val affectedPopDnaRatio = 10

    private val infectedNodes = nodes.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet
    private val infectedPop = nodes.values.map(_.infected).sum
    private val deceasedPop = nodes.values.map(_.died).sum

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] with incremented dna points
     */
    override def modifyFunction(state: SimulationState): Disease =
      val prevInfectedPop = state.world.nodes.values.map(_.infected).sum
      val prevInfectedNodes = state.world.nodes.filter(_._2.infected >= 0).keySet
      val prevDeceasedPop = state.world.nodes.values.map(_.died).sum

      val newInfectedNodes = infectedNodes -- prevInfectedNodes
      val newInfectedPop = Math.max(infectedPop - prevInfectedPop, 0)
      val newDeceasedPop = deceasedPop - prevDeceasedPop

      val pointsToAdd: Int = 
        newInfectedNodes.size * newNodeDnaMul +
        newInfectedPop % affectedPopDnaRatio +
        newDeceasedPop % affectedPopDnaRatio

      state.disease.addDnaPoints(pointsToAdd)

  /**
   * The [[Event]] used to check if the [[Disease]] will randomly evolve
   */
  case class Mutation() extends Event[Disease]:
    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Disease]] with a randomly evolved [[Symptom]]
     *         or the old one
     */
    override def modifyFunction(state: SimulationState): Disease =
      if state.disease.mutationChance >= Random.nextDouble()
      then state.disease.randomMutation()
      else state.disease


  /**
   * The [[Event]] used to slow down the [[Cure]] speed
   *
   * @param tr the [[Trait]] that slows down the [[Cure]]
   */
  case class CureSlowDown(tr: Trait) extends Event[Cure]:
    private val mod: Additive = Additive(ModifierId(ModifierSource.Mutation(MutationId(tr.name)) ,ModifierKind.Additive), -tr.stats.cureSlowdown)

    /**
     * @param state current [[SimulationState]] to be updated
     * @return a new instance of [[Cure]] with its speed updated
     */
    override def modifyFunction(state: SimulationState): Cure =
      state.cure.copy(modifiers = state.cure.modifiers.add(mod)).advance()
