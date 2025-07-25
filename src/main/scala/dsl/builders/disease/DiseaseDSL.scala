package dsl.builders.disease

import dsl.builders.SimulationStateBuilder
import model.plague.Trait

object DiseaseDSL:
  def disease(init: DiseaseBuilder ?=> Unit)(using
      ssb: SimulationStateBuilder
  ): Unit =
    var current: DiseaseBuilder = DiseaseBuilder()

    given diseaseBuilder: DiseaseBuilder =
      DiseaseBuilderProxy(() => current, updated => current = updated)

    init
    ssb.withDisease(diseaseBuilder.build())

  def diseaseName(init: DiseaseBuilder ?=> String)(using
      db: DiseaseBuilder
  ): Unit =
    db.withName(init)

  def diseaseTraits(init: DiseaseBuilder ?=> Set[Trait])(using
      db: DiseaseBuilder
  ): Unit =
    db.withTraits(init)

  def diseasePoints(init: DiseaseBuilder ?=> Int)(using
      db: DiseaseBuilder
  ): Unit =
    db.withPoints(init)
