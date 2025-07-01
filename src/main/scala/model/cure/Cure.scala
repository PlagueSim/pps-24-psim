package model.cure

final case class Cure(
    progress: Double = 0.0,
    baseSpeed: Double = 0.1,
    modifiers: CureModifiers = CureModifiers.empty
)

final case class CureModifiers(factors: List[CureModifier]) {
  def add(modifier: CureModifier): CureModifiers =
    CureModifiers(modifier :: factors)

  def remove(filter: CureModifier => Boolean): CureModifiers =
    CureModifiers(factors.filterNot(filter))
}

object CureModifiers {
  val empty: CureModifiers = CureModifiers(Nil)
}

trait CureModifier
