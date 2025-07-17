package view.plague

import model.core.SimulationState
import model.plague.Trait
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.{ListCell, ListView}
import view.updatables.UpdatableView

class TraitList(traits: Seq[Trait]) extends ListView[Trait](ObservableBuffer.from(traits)) with UpdatableView:
  cellFactory = (_: ListView[Trait]) =>
    new ListCell[Trait]:
      item.onChange((_, _, newItem) =>
        text = Option(newItem).map(_.name).orNull
      )

  margin = Insets(10)

  override def update(newState: SimulationState): Unit = 0
