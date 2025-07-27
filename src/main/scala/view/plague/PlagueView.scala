package view.plague

import controller.ViewController
import model.core.SimulationState
import model.plague.db.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import scalafx.scene.text.{Font, Text}
import view.StdButton
import view.updatables.UpdatableView

class PlagueView extends BorderPane with UpdatableView:
  private val controller = ViewController(this)

  private val transmissions = TraitsView(Transmissions.allBasics)
  private val symptoms = TraitsView(Symptoms.allBasics)
  private val abilities = TraitsView(Abilities.allBasics)

  private val plgName: Label = new Label(""):
    font = Font(18)
    padding = Insets(10)

  private val infectivityLabel = Label("")
  private val severityLabel = Label("")
  private val lethalityLabel = Label("")


  private val plagueInfos = new BorderPane():
    top = plgName
    center = new VBox:
      children = Seq(
        infectivityLabel,
        severityLabel,
        lethalityLabel
      )


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

  private def effectivenessLabels(effMap: Map[Any, Double]): Seq[Label] = effMap match
    case map if map.nonEmpty => Label("Effectiveness:") +:
      map.toSeq.map((k, v) => Label(s" - $k: ${"%.2f".format(v)}"))
    case _ => Seq.empty

  override def update(newState: SimulationState): Unit =
    val diseaseStats = newState.disease.allStats()
    plgName.text = newState.disease.name

    infectivityLabel.text = f"Infectivity: ${diseaseStats.infectivity}%.2f"
    severityLabel.text = f"Severity: ${diseaseStats.severity}%.2f"
    lethalityLabel.text = f"Lethality: ${diseaseStats.lethality}%.2f"
    // effectivenessLabels(diseaseStats.effectiveness)

    symptoms.update(newState)
    transmissions.update(newState)
    abilities.update(newState)
