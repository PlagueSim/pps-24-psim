package view

import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.text.TextAlignment.*

def showStartPopup(): Unit =
  val popupStage = new Stage:
    title = "Welcome"

  val startButton = StdButton("Start"):
      popupStage.close()

  popupStage.scene = new Scene:
    root = new VBox:
      spacing = 15
      alignment = Pos.Center
      padding = Insets(20)
      children = Seq(
        new Label():
          text = "Welcome to Plague-Sim, a minimalistic simulation game\n" +
            "about spreading a disease in to the world!\n" +
            "We know it's pretty macabre but De gustibus non est disputandum!\n" +
            "The objective of the game is to infect and kill all the displayed population\n" +
            "distributed in various part of the world before they cure you.\n" +
            "To do so you will need to upgrade the disease in the appropriate menu\n" +
            "using DNA points, gained during the spread of your disease.\n" +
            "Watch out to not evolve dangerous symptoms too fast!\n" +
            "they will spot you easily this way and cure you faster.\n" +
            "Focus on transmission first and good luck!"
          wrapText = true
          textAlignment = Center
        ,
        startButton
      )


  popupStage.showAndWait()