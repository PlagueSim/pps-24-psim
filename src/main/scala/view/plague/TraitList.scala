package view.plague

import model.plague.Trait
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView}

class TraitList(traits: Seq[Trait]) extends ListView[Trait](ObservableBuffer.from(traits)):
  cellFactory = (_: ListView[Trait]) =>
    new ListCell[Trait]:
      item.onChange((_, _, newItem) =>
        text = Option(newItem).map(_.name).orNull
      )
