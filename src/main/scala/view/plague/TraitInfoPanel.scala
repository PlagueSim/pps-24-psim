package view.plague

import scalafx.scene.layout.{BorderPane, VBox}
import model.plague.Trait
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.text.Font
import view.StdButton

class TraitInfoPanel(tr: Trait) extends BorderPane:
  private val nameLabel = new Label(s"${tr.name}"):
    font = Font(18)
    padding = Insets(10)

  private val evolveButton = StdButton("Evolve"):
    println(s"Evolved: ${tr.name}")

  private val infoPanel = new VBox():
    padding = Insets(10)
    children = Seq(
      Label(s"Cost: ${tr.cost}"),
      Label(f"Infectivity: ${tr.infectivity}%.2f"),
      Label(f"Severity: ${tr.severity}%.2f"),
      Label(f"Lethality: ${tr.lethality}%.2f"),
      evolveButton
    )
  top = nameLabel
  center = infoPanel
  bottom = evolveButton


