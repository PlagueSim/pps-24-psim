package model.cure

final case class Cure(
    progress: Double = 0.0,
    baseSpeed: Double = 0.1,
    modifiers: CureModifiers = CureModifiers.empty
)

final case class CureModifiers(modifiers: List[CureModifier])

object CureModifiers {
  val empty: CureModifiers = CureModifiers(Nil)
}

final case class CureModifier()
