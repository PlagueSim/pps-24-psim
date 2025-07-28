package model.plague.db

import model.plague
import model.plague.Trait
import model.plague.TraitCategory.Ability
import model.plague.TraitDsl.*
import model.plague.placehoderTypes.{Cold, Hot, Rich}

/**
 * An object containing all basic Ability values
 */
object Abilities extends TraitContainer:

  final val coldResistance1: Trait = define("Cold Resistance 1", Ability)
    .cost(7)
    .effectiveness(Map(Cold->0.3))
    .build()

  final val coldResistance2: Trait = define("Cold Resistance 2", Ability)
    .cost(12)
    .effectiveness(Map(Cold->0.6))
    .prerequisite("Cold Resistance 1")
    .build()

  final val heatResistance1: Trait = define("Heat Resistance 1", Ability)
    .cost(11)
    .effectiveness(Map(Hot -> 0.3))
    .build()

  final val heatResistance2: Trait = define("Heat Resistance 2", Ability)
    .cost(22)
    .effectiveness(Map(Hot -> 0.6))
    .prerequisite("Heat Resistance 1")
    .build()

  final val environmentalHardening: Trait = define("Environmental Hardening", Ability)
    .cost(30)
    .effectiveness(Map(
      Hot -> 1,
      Cold -> 1
    ))
    .prerequisite("Heat Resistance 2", "Cold Resistance 2")
    .build()

  final val drugResistance1: Trait = define("Drug Resistance 1", Ability)
    .cost(11)
    .effectiveness(Map(Rich -> 0.3))
    .build()

  final val drugResistance2: Trait = define("Drug Resistance 2", Ability)
    .cost(25)
    .effectiveness(Map(Rich -> 0.7))
    .prerequisite("Drug Resistance 1")
    .build()

  final val geneticHardening1: Trait = define("Genetic Hardening 1", Ability)
    .cost(7)
    .cureSlowdown(0.1)
    .prerequisite("Drug Resistance 1")
    .build()

  final val geneticHardening2: Trait = define("Genetic Hardening 2", Ability)
    .cost(22)
    .prerequisite("Genetic Hardening 1")
    .cureSlowdown(0.3)
    .build()

  final val geneticReShuffle1: Trait = define("Genetic Reshuffle 1", Ability)
    .cost(17)
    .cureReset(0.25)
    .prerequisite("Drug Resistance 1")
    .build()

  final val geneticReShuffle2: Trait = define("Genetic Reshuffle 2", Ability)
    .cost(21)
    .prerequisite("Genetic Reshuffle 1")
    .cureReset(0.25)
    .build()

  final val geneticReShuffle3: Trait = define("Genetic ReShuffle 3", Ability)
    .cost(25)
    .prerequisite("Genetic Reshuffle 2")
    .cureReset(0.25)
    .build()

  def allBasics: List[Trait] = List(
    coldResistance1, coldResistance2, heatResistance1, heatResistance2, environmentalHardening,
    drugResistance1, drugResistance2, geneticHardening1, geneticHardening2,
    geneticReShuffle1, geneticReShuffle2, geneticReShuffle3
  )