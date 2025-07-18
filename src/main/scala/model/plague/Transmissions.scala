package model.plague

import model.plague.TraitCategory.Transmission
import model.plague.TraitDsl.*

import placehoderTypes.*

/**
 * An object containing all basic Transmission values
 */
object Transmissions:

  final val bird1: Trait = define("Bird 1", Transmission)
    .cost(12)
    .infectivity(3.0)
    .effectiveness(Map(Land -> 9))
    .mutationChance(0.01)
    .build()

  final val rodent1: Trait = define("Rodent 1", Transmission)
    .cost(10)
    .infectivity(3.0)
    .effectiveness(Map(Urban -> 0.8))
    .mutationChance(0.005)
    .build()

  final val livestock1: Trait = define("Livestock 1", Transmission)
    .cost(7)
    .infectivity(2.0)
    .effectiveness(Map(Rural -> 0.8))
    .mutationChance(0.005)
    .build()

  final val blood1: Trait = define("Blood 1", Transmission)
    .cost(8)
    .infectivity(2.0)
    .effectiveness(Map(Poor -> 0.8))
    .mutationChance(0.005)
    .build()

  final val insect1: Trait = define("Insect 1", Transmission)
    .cost(9)
    .infectivity(4.0)
    .effectiveness(Map(Hot -> 0.1))
    .mutationChance(0.005)
    .build()

  final val air1: Trait = define("Air 1", Transmission)
    .cost(9)
    .infectivity(4.0)
    .effectiveness(Map(Air -> 9, Arid -> 0.8))
    .build()

  final val water1: Trait = define("Water 1", Transmission)
    .cost(9)
    .infectivity(4.0)
    .effectiveness(Map(Water -> 9, Humid -> 0.8))
    .build()

  final val bird2: Trait = define("Bird 2", Transmission)
    .cost(18)
    .infectivity(6.0)
    .effectiveness(Map(Land -> 90))
    .mutationChance(0.01)
    .prerequisite("Bird 1")
    .build()

  final val rodent2: Trait = define("Rodent 2", Transmission)
    .cost(16)
    .infectivity(6.0)
    .effectiveness(Map(Urban -> 1.2))
    .mutationChance(0.005)
    .prerequisite("Rodent 1")
    .build()

  final val livestock2: Trait = define("Livestock 2", Transmission)
    .cost(12)
    .infectivity(4.0)
    .effectiveness(Map(Rural -> 1.2))
    .mutationChance(0.005)
    .prerequisite("Livestock 1")
    .build()

  final val blood2: Trait = define("Blood 2", Transmission)
    .cost(13)
    .infectivity(4.0)
    .effectiveness(Map(Poor -> 1.2))
    .mutationChance(0.005)
    .prerequisite("Blood 1")
    .build()

  final val insect2: Trait = define("Insect 2", Transmission)
    .cost(20)
    .infectivity(8.0)
    .effectiveness(Map(Hot -> 0.3))
    .mutationChance(0.005)
    .prerequisite("Insect 1")
    .build()

  final val air2: Trait = define("Air 2", Transmission)
    .cost(14)
    .infectivity(7.0)
    .effectiveness(Map(Air -> 90, Arid -> 1.2))
    .prerequisite("Air 1")
    .build()

  final val water2: Trait = define("Water 2", Transmission)
    .cost(15)
    .infectivity(8.0)
    .effectiveness(Map(Water -> 90, Humid -> 1.2))
    .prerequisite("Water 1")
    .build()

  final var extremeZoonosis: Trait = define("Extreme Zoonosis", Transmission)
    .cost(22)
    .infectivity(5)
    .effectiveness(Map(Land -> 10, Urban -> 1, Rural -> 1))
    .mutationChance(0.03)
    .prerequisite("Livestock 2", "Bird 2", "Rodent 2")
    .build()

  final var extremeHematophagy: Trait = define("Extreme Hematophagy", Transmission)
    .cost(24)
    .infectivity(5)
    .effectiveness(Map(Poor -> 1, Hot -> 0.3))
    .mutationChance(0.01)
    .prerequisite("Insect 2", "Blood 2")
    .build()

  final var extremeBioaerosol: Trait = define("Extreme Bioaerosol", Transmission)
    .cost(16)
    .infectivity(5)
    .effectiveness(Map(Air -> 20, Water -> 20, Humid -> 1.3, Arid -> 1.3))
    .prerequisite("Air 2", "Water 2")
    .build()

  def allBasics: List[Trait] = List(
    air1, air2, water1, water2, extremeBioaerosol,
    bird1, bird2, livestock1, livestock2, rodent1, rodent2, extremeZoonosis,
    blood1, blood2, insect1, insect2, extremeHematophagy
  )

