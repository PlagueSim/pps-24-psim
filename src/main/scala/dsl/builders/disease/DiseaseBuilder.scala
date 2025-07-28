package dsl.builders.disease

import model.plague.Trait
import model.plague.Disease

/** A builder for creating instances of [[Disease]].
  */
case class DiseaseBuilder(
    name: String = "name",
    traits: Set[Trait] = Set.empty,
    points: Int = 0
):
  /** Sets the name of the disease.
    */
  def withName(name: String): DiseaseBuilder =
    copy(name = name)

  /** Sets the traits of the disease.
    */
  def withTraits(traits: Set[Trait]): DiseaseBuilder =
    copy(traits = traits)

  /** Sets the points for the disease.
    */
  def withPoints(points: Int): DiseaseBuilder =
    copy(points = points)

  /** Builds and returns a [[Disease]] instance with the configured parameters.
    */
  def build(): Disease =
    Disease(name, traits, points)
