package dsl.builders.cure

import model.cure.CureModifiers
import model.cure.Cure

/** A builder for creating instances of [[Cure]].
  */
case class CureBuilder(
    progress: Double = 0.0,
    baseSpeed: Double = 0.0,
    modifiers: CureModifiers = CureModifiers.empty
):
  /** Sets the progress of the cure.
    */
  def withProgress(progress: Double): CureBuilder =
    require(
      progress >= 0.0 && progress <= 1.0,
      "Progress must be between 0.0 and 1.0"
    )
    copy(progress = progress)

  /** Sets the base speed of the cure's development.
    */
  def withBaseSpeed(baseSpeed: Double): CureBuilder =
    require(baseSpeed >= 0.0, "Base speed must be non-negative")
    copy(baseSpeed = baseSpeed)

  /** Sets the modifiers for the cure.
    */
  def withModifiers(modifiers: CureModifiers): CureBuilder =
    copy(modifiers = modifiers)

  /** Builds and returns a [[Cure]] instance with the configured parameters.
    */
  def build(): Cure =
    Cure(progress, baseSpeed, modifiers)
