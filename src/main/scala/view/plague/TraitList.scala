package view.plague

import model.core.SimulationState
import model.plague.Trait
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.{ListCell, ListView}
import view.updatables.UpdatableView

/**
 * The panel that shows a list of all Traits and abilitates
 * the player to eventually evolve the Disease
 *
 * @param traits The Traits shown in the list
 */
class TraitList(traits: Seq[Trait]) extends ListView[Trait](ObservableBuffer.from(traits)) with UpdatableView:

  margin = Insets(10)

  override def update(newState: SimulationState): Unit =
    cellFactory = (_: ListView[Trait]) =>
      new ListCell[Trait]:
        item.onChange((_, _, newItem) =>
          text = Option(newItem).map(_.name).orNull

          if newItem != null && !newState.disease.canEvolve(newItem) then
            disable = true
            style = "-fx-background-color: lightgray; -fx-text-fill: darkgray;"
          else if newState.disease.traits.contains(newItem) then
            disable = false
            style = "-fx-background-color: palegreen; -fx-text-fill: darkgreen;"
          else
            disable = false
            style = ""
        )
