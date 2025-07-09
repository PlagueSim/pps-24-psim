package view.plague

import model.plague.Trait
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, Label, ListCell, ListView}
import scalafx.scene.layout.VBox
import scalafx.geometry.Insets

class TraitList(traits: Seq[Trait], rightPaneSetter: VBox => Unit) extends ListView[Trait](ObservableBuffer.from(traits)):

  cellFactory = (_: ListView[Trait]) =>
    new ListCell[Trait]:
      item.onChange((_, _, newItem) =>
        text = Option(newItem).map(_.name).orNull
      )

  selectionModel().selectedItemProperty.onChange { (_, _, selectedTrait) =>
    if selectedTrait != null then
      val evolveButton = new Button("Evolvi"):
        onAction = _ => println(s"Evolvo: ${selectedTrait.name}")

      val infoPanel = new VBox(10):
          padding = Insets(10)
          children = Seq(
            new Label(s"Nome: ${selectedTrait.name}"),
            new Label(f"Infectivity: ${selectedTrait.infectivity}%.2f"),
            new Label(f"Severity: ${selectedTrait.severity}%.2f"),
            new Label(f"Lethality: ${selectedTrait.lethality}%.2f"),
            evolveButton
          )

      rightPaneSetter(infoPanel)
    else
      rightPaneSetter(null)
  }
