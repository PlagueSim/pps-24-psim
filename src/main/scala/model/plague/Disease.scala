package model.plague


/**
 * TODO
 * @param name
 * @param diseaseType
 * @param traits
 * @param dnaPoints
 */
case class Disease private(
                    name: String = "pax-12",
                    traits: Set[Trait] = Set.empty,
                    dnaPoints: Int = 0
                  ):
  
  def infectivity: Double = traits.map(_.infectivity).sum

  def severity: Double = traits.map(_.severity).sum

  def lethality: Double = traits.map(_.lethality).sum
    
  private def hasTrait(name: String): Boolean = traits.exists(_.name == name)

  private def canEvolve(t: Trait): Boolean = t.prerequisites.exists(hasTrait)

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
