package view.plague

import model.plague.Symptoms
import scalafx.geometry.Insets
import scalafx.scene.layout.BorderPane
import scalafx.Includes.jfxReadOnlyObjectProperty2sfx

class SymptomsView extends BorderPane:

  private val symptomList = TraitList(Symptoms.allBasics)

  symptomList.selectionModel().selectedItemProperty().onChange((_, _, selectedTrait) =>
    if selectedTrait != null then
      val infoPanel = TraitInfoPanel(selectedTrait)
      right = infoPanel
  )

  center = symptomList
  padding = Insets(10)
