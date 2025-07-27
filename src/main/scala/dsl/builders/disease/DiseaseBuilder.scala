package dsl.builders.disease

import model.plague.Trait
import model.plague.Disease

case class DiseaseBuilder(
    name: String = "name",
    traits: Set[Trait] = Set.empty,
    points: Int = 0
):
  def withName(name: String): DiseaseBuilder =
    copy(name = name)

  def withTraits(traits: Set[Trait]): DiseaseBuilder =
    copy(traits = traits)

  def withPoints(points: Int): DiseaseBuilder =
    copy(points = points)

  def build(): Disease =
    Disease(name, traits, points)
