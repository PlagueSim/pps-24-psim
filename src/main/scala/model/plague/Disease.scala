package model.plague

import model.plague.TraitCategory.*

import scala.util.Random
/**
 * TODO
 * @param name
 * @param diseaseType
 * @param traits
 * @param dnaPoints
 */
case class Disease private(
                    name: String,
                    traits: Set[Trait],
                    dnaPoints: Int
                  ):
  /**
   * Calculates the total infectivity of the disease.
   *
   * Infectivity is the sum of the infectivity values of all currently evolved traits.
   *
   * @return A `Double` representing the disease's current infectivity.
   */
  def infectivity: Double = traits.toList.map(_.infectivity).sum

  /**
   * Calculates the total severity of the disease.
   *
   * Severity is the sum of the severity values of all currently evolved traits.
   *
   * @return A `Double` representing the disease's current severity.
   */
  def severity: Double = traits.toList.map(_.severity).sum

  /**
   * Calculates the total lethality of the disease.
   *
   * Lethality is the sum of the lethality values of all currently evolved traits.
   *
   * @return A `Double` representing the disease's current lethality.
   */
  def lethality: Double = traits.toList.map(_.lethality).sum

  /**
   *
   * @param name
   * @return
   */
  private def hasTrait(name: String): Boolean = traits.exists(_.name == name)

  /**
   * Determines whether the given trait can be evolved based on its prerequisites.
   *
   * Evolution rules differ by trait category:
   * - For `Symptom` traits: evolution is allowed if the trait has no prerequisites,
   * or if at least one of its prerequisites has already been evolved.
   * - For all other categories (e.g., `Transmission`, `Ability`): all prerequisites must be met.
   *
   * @param t The trait to check for evolvability.
   * @return `true` if the trait can be evolved under current conditions, `false` otherwise.
   */
  private def canEvolve(t: Trait): Boolean = t.category match
    case Symptom => t.prerequisites.exists(hasTrait) || t.prerequisites.isEmpty
    case _ => t.prerequisites.forall(hasTrait)

  /**
   *
   * @param traitToAdd
   * @return
   */
  def evolve(traitToAdd: Trait): Either[String, Disease] =
    if hasTrait(traitToAdd.name) then Left(s"${traitToAdd.name} already evolved.")
    else if !canEvolve(traitToAdd) then
      val missing = traitToAdd.prerequisites.mkString(", ")
      Left(s"${traitToAdd.name} is locked. Missing any of: $missing")
    else if dnaPoints < traitToAdd.cost then Left(s"Not enough DNA points to evolve ${traitToAdd.name}")
    else Right(copy(
        traits = traits + traitToAdd,
        dnaPoints = dnaPoints - traitToAdd.cost
      ))

  /**
   *
   * @param allTraits
   * @return
   */
  def randomMutation(allTraits: Set[Trait]): Disease =
    val unevolved = allTraits.diff(traits)
    val evolvable = unevolved.filter(t => canEvolve(t))

    if evolvable.isEmpty then this
    else
      val chosen = Random.shuffle(evolvable.toList).head
      copy(traits = traits + chosen)

  /**
   *
   * @param points
   * @return
   */
  def addDnaPoints(points: Int): Disease =
    copy(dnaPoints = dnaPoints + points)

/**
 * TODO
 */
object Disease:
  def apply(
             name: String = "pax-12",
             traits: Set[Trait] = Set.empty,
             dnaPoints: Int
           ): Disease = new Disease(name, traits, dnaPoints)
