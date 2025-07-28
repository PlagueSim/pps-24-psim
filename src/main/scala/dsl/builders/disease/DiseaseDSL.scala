package dsl.builders.disease

import dsl.builders.SimulationState.SimulationStateBuilder
import model.plague.Trait

/** A DSL for configuring a disease within the simulation. */
object DiseaseDSL:
  /** Defines a disease configuration block.
    */
  def disease(init: DiseaseBuilder ?=> Unit)(using
      ssb: SimulationStateBuilder
  ): Unit =
    var current: DiseaseBuilder = DiseaseBuilder()

    given diseaseBuilder: DiseaseBuilder =
      DiseaseBuilderProxy(() => current, updated => current = updated)

    init
    ssb.withDisease(diseaseBuilder.build())

  /** Sets the name of the disease.
    */
  def diseaseName(init: DiseaseBuilder ?=> String)(using
      db: DiseaseBuilder
  ): Unit =
    db.withName(init)

  /** Sets the traits of the disease.
    */
  def diseaseTraits(init: DiseaseBuilder ?=> Set[Trait])(using
      db: DiseaseBuilder
  ): Unit =
    db.withTraits(init)

  /** Sets the initial points for the disease.
    */
  def diseasePoints(init: DiseaseBuilder ?=> Int)(using
      db: DiseaseBuilder
  ): Unit =
    db.withPoints(init)
