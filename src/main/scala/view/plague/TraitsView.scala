package view.plague

import model.core.SimulationState
import model.plague.Trait
import scalafx.geometry.Insets
import scalafx.scene.layout.BorderPane
import scalafx.Includes.jfxReadOnlyObjectProperty2sfx
import view.updatables.UpdatableView

class TraitsView(allTraits: Seq[Trait]) extends BorderPane with UpdatableView:

  private val traitList: TraitList = TraitList(allTraits)
  private val infoPanels: Map[String, TraitInfoPanel] = allTraits.map(t=> (t.name, TraitInfoPanel(t))).toMap

  traitList.selectionModel().selectedItemProperty().onChange((_, _, selectedTrait) =>
    if selectedTrait != null then
      val infoPanel = infoPanels(selectedTrait.name)
      right = infoPanel
  )

  center = traitList
  padding = Insets(10)

  override def update(newState: SimulationState): Unit =
    traitList.update(newState)
    infoPanels.values.foreach(_.update(newState))