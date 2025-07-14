package model.events
import model.core.SimulationState
import model.plague.{Disease, Trait}

import scala.util.Random

object DiseaseEvents:

  case class Evolution(traitToEvolve: Trait) extends Event[Disease]:
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
