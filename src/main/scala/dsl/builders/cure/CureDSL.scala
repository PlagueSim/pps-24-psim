package dsl.builders.cure

object CureDSL:
  def cure(init: CureBuilder ?=> Unit)(using
      ssb: dsl.builders.SimulationStateBuilder
  ): Unit =
    var current: CureBuilder       = CureBuilder()
    given cureBuilder: CureBuilder =
      CureBuilderProxy(() => current, updated => current = updated)
    init
    ssb.withCure(cureBuilder.build())

  def cureProgress(init: CureBuilder ?=> Double)(using
      cb: CureBuilder
  ): Unit =
    cb.withProgress(init)

  def cureBaseSpeed(init: CureBuilder ?=> Double)(using
      cb: CureBuilder
  ): Unit =
    cb.withBaseSpeed(init)

  def cureModifiers(init: CureBuilder ?=> model.cure.CureModifiers)(using
      cb: CureBuilder
  ): Unit =
    cb.withModifiers(init)
