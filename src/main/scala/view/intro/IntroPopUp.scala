package view.intro

import model.core.SimulationState
import model.world.Node
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.layout.VBox
import scalafx.scene.text.TextAlignment.*
import scalafx.stage.Stage
import view.StdButton

def showStartPopup(state: SimulationState): SimulationState =
  type WorldNode = (String, Node)

  var updatedState = state
  var selectedNode: Option[WorldNode] = None

  val popupStage = new Stage:
    title = "Welcome"

  val startButton = StdButton("Start"):
    selectedNode.foreach(node =>
      val (key, n) = node
      val updatedNodes = state.world.nodes.updated(key, n.increaseInfection(1))
      val updatedWorld = state.world.modifyNodes(updatedNodes)
      updatedState = state.replace(updatedWorld)
    )
    popupStage.close()
  startButton.disable = true

  val nodesBuffer = ObservableBuffer.from(state.world.nodes)
  val nodeListView = new ListView[WorldNode](nodesBuffer):
    prefHeight = 150
    onMouseClicked = _ =>
      selectedNode = Option(selectionModel().getSelectedItem)
      startButton.disable = selectedNode.isEmpty

  val intro = introLabel()

  popupStage.scene = new Scene:
    root = new VBox:
      spacing = 15
      alignment = Pos.Center
      padding = Insets(20)
      children = Seq(intro, nodeListView, startButton)
  popupStage.showAndWait()
  updatedState

private def introLabel(): Label =
  new Label():
    text = "Welcome to Plague-Sim, a minimalistic simulation game\n" +
      "about spreading a disease in to the world!\n" +
      "We know it's pretty macabre but De gustibus non est disputandum!\n" +
      "The objective of the game is to infect and kill all the displayed population\n" +
      "distributed in various part of the world before they cure you.\n" +
      "To do so you will need to upgrade the disease in the appropriate menu\n" +
      "using DNA points, gained during the spread of your disease.\n" +
      "Watch out to not evolve dangerous symptoms too fast!\n" +
      "they will spot you easily this way and cure you faster.\n\n" +
      "Now choose where you want to begin your infection, focus on transmission first and good luck!"
    wrapText = true
    textAlignment = Center