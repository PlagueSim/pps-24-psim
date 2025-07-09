package view

import scalafx.scene.layout.BorderPane
import scalafx.geometry.Insets
import model.plague.Symptoms
import view.plague.TraitList

class SymptomsView extends BorderPane:

  private val symptomList = TraitList(Symptoms.allBasics, node => this.right = node)

  center = symptomList
  padding = Insets(10)

