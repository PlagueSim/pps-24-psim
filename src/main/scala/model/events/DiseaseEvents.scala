package model.events
import model.core.SimulationState
import model.cure.{Cure, CureModifier, ModifierId, ModifierKind, ModifierSource, MutationId}
import model.cure.CureModifier.Additive
import model.plague.{Disease, Trait}

import scala.util.Random

object DiseaseEvents:

  case class Evolution(traitToEvolve: Trait) extends Event[Disease]:
    if traitToEvolve.stats.cureSlowdown != 0 then CureEventBuffer.newEvent(CureSlowDown(traitToEvolve))

    override def modifyFunction(state: SimulationState): Disease =
      state.disease.evolve(traitToEvolve) match
        case Left(str) => state.disease
        case Right(disease) => disease

  case class Involution(traitToRemove: Trait) extends Event[Disease]:
    override def modifyFunction(state: SimulationState): Disease =
      state.disease.involve(traitToRemove) match
        case Left(str) => state.disease
        case Right(disease) => disease

  case class DnaPointsAddition(pointsToAdd: Int) extends Event[Disease]:
    override def modifyFunction(state: SimulationState): Disease =
      state.disease.addDnaPoints(pointsToAdd)

  case class Mutation() extends Event[Disease]:
    override def modifyFunction(state: SimulationState): Disease =
      if state.disease.mutationChance >= Random.nextDouble()
      then state.disease.randomMutation()
      else state.disease


  case class CureSlowDown(tr: Trait) extends Event[Cure]:
    val mod: Additive = Additive(ModifierId(ModifierSource.Mutation(MutationId(tr.name)) ,ModifierKind.Additive), -tr.stats.cureSlowdown)

    override def modifyFunction(state: SimulationState): Cure =
      state.cure.copy(modifiers = state.cure.modifiers.add(mod)).advance()
