package view.plague

import controller.ViewController
import model.core.SimulationState
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import scalafx.scene.text.{Font, Text}
import view.StdButton
import view.updatables.UpdatableView

class PlagueView extends BorderPane with UpdatableView:
  private val controller = ViewController(this)

  private val transmissions = TransmissionView()
  private val symptoms = SymptomsView()
  private val abilities = AbilityView()

  private val plgName: Label = new Label(""):
    font = Font(18)
    padding = Insets(10)

  private val infectivityLabel = Label("")
  private val severityLabel = Label("")
  private val lethalityLabel = Label("")
  private val traits = TextField()


  private val plagueInfos = new BorderPane():
    top = plgName
    center = new VBox:
      children = Seq(
        infectivityLabel,
        severityLabel,
        lethalityLabel
      )
    bottom = traits




  private val trsBtn = StdButton("Transmission"):
    controller.show(transmissions)
  private val smptsBtn = StdButton("Symptoms"):
    controller.show(symptoms)
  private val ablBtn = StdButton("Abilities"):
    controller.show(abilities)
  private val topBar = new HBox:
    children = Seq(trsBtn, smptsBtn, ablBtn)


  padding = Insets(10)
  left = plagueInfos
  top = topBar

  override def update(newState: SimulationState): Unit =
    plgName.text = newState.disease.name
    infectivityLabel.text = f"Infectivity: ${newState.disease.infectivity}%.2f"
    severityLabel.text = f"Severity: ${newState.disease.severity}%.2f"
    lethalityLabel.text = f"Lethality: ${newState.disease.lethality}%.2f"
    traits.text = s"${newState.disease.traits.map(_.name).toString()}"

