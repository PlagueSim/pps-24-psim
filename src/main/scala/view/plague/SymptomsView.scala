package view.plague

import model.core.SimulationState
import model.plague.Symptoms
import scalafx.geometry.Insets
import scalafx.scene.layout.BorderPane
import scalafx.Includes.jfxReadOnlyObjectProperty2sfx
import view.updatables.UpdatableView

class SymptomsView extends BorderPane with UpdatableView:

  private val symptomList = TraitList(Symptoms.allBasics)

  symptomList.selectionModel().selectedItemProperty().onChange((_, _, selectedTrait) =>
    if selectedTrait != null then
      val infoPanel = TraitInfoPanel(selectedTrait)
      right = infoPanel
  )

  center = symptomList
  padding = Insets(10)

  override def update(newState: SimulationState): Unit =
    symptomList.update(newState)