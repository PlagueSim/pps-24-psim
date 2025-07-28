package dsl.builders.disease

import model.plague.{Disease, Trait}

/** A proxy for [[DiseaseBuilder]] that allows modifying a disease builder
  * instance through a getter and a setter.
  */
class DiseaseBuilderProxy(
    get: () => DiseaseBuilder,
    set: DiseaseBuilder => Unit
) extends DiseaseBuilder:
  /** Sets the name of the disease and updates the underlying builder instance.
    */
  override def withName(name: String): DiseaseBuilder =
    val updated = get().withName(name)
    set(updated)
    updated

  /** Sets the traits of the disease and updates the underlying builder
    * instance.
    */
  override def withTraits(traits: Set[Trait]): DiseaseBuilder =
    val updated = get().withTraits(traits)
    set(updated)
    updated

  /** Sets the points for the disease and updates the underlying builder
    * instance.
    */
  override def withPoints(points: Int): DiseaseBuilder =
    val updated = get().withPoints(points)
    set(updated)
    updated

  /** Builds and returns a [[Disease]] instance using the
    * underlying builder.
    */
  override def build(): Disease =
    get().build()
