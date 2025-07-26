package dsl.builders.cure

class CureBuilderProxy(get: () => CureBuilder, set: CureBuilder => Unit)
    extends CureBuilder:
  override def withProgress(progress: Double): CureBuilder =
    val updated = get().withProgress(progress)
    set(updated)
    updated

  override def withBaseSpeed(baseSpeed: Double): CureBuilder =
    val updated = get().withBaseSpeed(baseSpeed)
    set(updated)
    updated

  override def withModifiers(modifiers: model.cure.CureModifiers): CureBuilder =
    val updated = get().withModifiers(modifiers)
    set(updated)
    updated

  override def build(): model.cure.Cure =
    get().build()
