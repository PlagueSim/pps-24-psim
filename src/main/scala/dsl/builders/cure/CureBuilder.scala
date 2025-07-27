package dsl.builders.cure

import model.cure.CureModifiers
import model.cure.Cure

case class CureBuilder(
    progress: Double = 0.0,
    baseSpeed: Double = 0.0,
    modifiers: CureModifiers = CureModifiers.empty
):
  def withProgress(progress: Double): CureBuilder =
    require(
      progress >= 0.0 && progress <= 1.0,
      "Progress must be between 0.0 and 1.0"
    )
    copy(progress = progress)

  def withBaseSpeed(baseSpeed: Double): CureBuilder =
    require(baseSpeed >= 0.0, "Base speed must be non-negative")
    copy(baseSpeed = baseSpeed)

  def withModifiers(modifiers: CureModifiers): CureBuilder =
    copy(modifiers = modifiers)

  def build(): Cure =
    Cure(progress, baseSpeed, modifiers)
