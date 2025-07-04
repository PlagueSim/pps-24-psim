package model.plague

/**
 * 
 */
enum TraitCategory:
  case Symptom, Transmission, Ability

/**
 * TODO
 *
 * @param name
 * @param category
 * @param infectivity
 * @param severity
 * @param lethality
 * @param cost
 * @param prerequisites
 */
case class Trait private(
                  name: String,
                  category: TraitCategory,
                  infectivity: Double,
                  severity: Double,
                  lethality: Double,
                  cost: Int,
                  prerequisites: Set[String]
                )

/**
 * TODO
 */
object Trait:
  def apply(
             name: String,
             category: TraitCategory,
             infectivity: Double = 0.0,
             severity: Double = 0.0,
             lethality: Double = 0.0,
             cost: Int = 0,
             prerequisites: Set[String] = Set.empty
           ): Trait = new Trait(name, category, infectivity, severity, lethality, cost, prerequisites)
