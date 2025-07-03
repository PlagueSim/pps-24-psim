package model.plague

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
   *
   * @return
   */
  def infectivity: Double = traits.map(_.infectivity).sum

  /**
   *
   * @return
   */
  def severity: Double = traits.map(_.severity).sum

  /**
   *
   * @return
   */
  def lethality: Double = traits.map(_.lethality).sum

  /**
   *
   * @param name
   * @return
   */
  private def hasTrait(name: String): Boolean = traits.exists(_.name == name)

  /**
   *
   * @param t
   * @return
   */
  private def canEvolve(t: Trait): Boolean =
    t.prerequisites.exists(hasTrait) || t.prerequisites.isEmpty

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
