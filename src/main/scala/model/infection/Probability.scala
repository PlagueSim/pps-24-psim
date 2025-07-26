package model.infection

object Probability:
  opaque type Probability = Double

  object Probability:
    def fromPercentage(p: Double): Probability = (p / 100.0).max(0).min(1)

    extension (p: Probability) def value: Double = p
