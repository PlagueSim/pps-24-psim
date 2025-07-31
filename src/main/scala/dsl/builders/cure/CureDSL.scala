package dsl.builders.cure

import dsl.builders.SimulationState.SimulationStateBuilder
import model.cure.CureModifiers

/** A DSL for configuring the cure within the simulation. */
object CureDSL:

  /** Defines a cure configuration block.
    */
  def cure(init: CureBuilder ?=> Unit)(using
      ssb: SimulationStateBuilder
  ): Unit =
    var current: CureBuilder       = CureBuilder()
    given cureBuilder: CureBuilder =
      CureBuilderProxy(() => current, updated => current = updated)
    init
    ssb.withCure(cureBuilder.build())

  /** Sets the progress of the cure.
    */
  def cureProgress(init: CureBuilder ?=> Double)(using
      cb: CureBuilder
  ): Unit =
    cb.withProgress(init)

  /** Sets the base speed of the cure's development.
    */
  def cureBaseSpeed(init: CureBuilder ?=> Double)(using
      cb: CureBuilder
  ): Unit =
    cb.withBaseSpeed(init)

  /** Sets the modifiers for the cure.
    */
  def cureModifiers(init: CureBuilder ?=> CureModifiers)(using
      cb: CureBuilder
  ): Unit =
    cb.withModifiers(init)
