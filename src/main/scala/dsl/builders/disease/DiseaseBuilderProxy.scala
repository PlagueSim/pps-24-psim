package dsl.builders.disease

class DiseaseBuilderProxy(
    get: () => DiseaseBuilder,
    set: DiseaseBuilder => Unit
) extends DiseaseBuilder:
  override def withName(name: String): DiseaseBuilder = {
    val updated = get().withName(name)
    set(updated)
    updated
  }

  override def withTraits(traits: Set[model.plague.Trait]): DiseaseBuilder = {
    val updated = get().withTraits(traits)
    set(updated)
    updated
  }

  override def withPoints(points: Int): DiseaseBuilder = {
    val updated = get().withPoints(points)
    set(updated)
    updated
  }

  override def build(): model.plague.Disease =
    get().build()
