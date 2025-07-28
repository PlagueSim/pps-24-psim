package dsl.builders.cure

import model.cure.{Cure, CureModifiers}

/** A proxy for [[CureBuilder]] that allows modifying a cure builder instance
  * through a getter and a setter.
  */
class CureBuilderProxy(get: () => CureBuilder, set: CureBuilder => Unit)
    extends CureBuilder:

  /** Sets the progress of the cure and updates the underlying builder instance.
    */
  override def withProgress(progress: Double): CureBuilder =
    val updated = get().withProgress(progress)
    set(updated)
    updated

  /** Sets the base speed of the cure's development and updates the underlying
    * builder instance.
    */
  override def withBaseSpeed(baseSpeed: Double): CureBuilder =
    val updated = get().withBaseSpeed(baseSpeed)
    set(updated)
    updated

  /** Sets the modifiers for the cure and updates the underlying builder
    * instance.
    */
  override def withModifiers(modifiers: CureModifiers): CureBuilder =
    val updated = get().withModifiers(modifiers)
    set(updated)
    updated

  /** Builds and returns a [[Cure]] instance using the underlying
    * builder.
    */
  override def build(): Cure =
    get().build()
