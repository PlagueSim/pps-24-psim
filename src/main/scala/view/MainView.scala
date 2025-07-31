package view

import model.world.WorldFactory
import controller.ViewController
import model.core.SimulationState
import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.layout.{BorderPane, VBox}
import view.cure.CureProgressBar
import view.plague.PlagueView
import view.updatables.UpdatableView
import view.world.SetupWorldFactory

/**
 * The main graphic interface of the game containing the world and plague views
 */
class MainView extends BorderPane with UpdatableView:
  private val controller = ViewController(this)

  private val setup = SetupWorldFactory.initializeWorldGui(WorldFactory.mockWorld())
  private val mapView = setup.worldView
  private val mapPane: Node = mapView.root
  private val plgPane = PlagueView()
  private val controlPane = ControlPane(controller)
  private val datePane = DatePane()
  private val progressBar = CureProgressBar()
  private val dnaPoints = DnaPointsCounter
  private val infectionRecap = InfectionRecap

  /**
   * The pane containing the buttons to switch between pages
   */
  private object ControlPane:
    def apply(controller: ViewController): BorderPane = new BorderPane:
      private val plagueButton = StdButton("Plague"):
        controller.show(plgPane)

      private val worldButton = StdButton("World"):
        controller.show(mapPane)

      left = plagueButton
      right = worldButton
      padding = Insets(10)
  end ControlPane
  controlPane.top = dnaPoints
  controlPane.center = new VBox():
    alignment = Center
    children = Seq(infectionRecap, progressBar)

  center = mapPane
  bottom = controlPane
  top = datePane

  override def update(newState: SimulationState): Unit =
    dnaPoints.update(newState)
    plgPane.update(newState)
    datePane.update(newState)
    progressBar.update(newState)
    mapView.update(newState)
    infectionRecap.update(newState)
end MainView

/**
 *  Displays current date of simulation
 */
class DatePane extends BorderPane with UpdatableView:
  private val dateLabel = Label("date")
  left = dateLabel
  padding = Insets(10)

  override def update(newState: SimulationState): Unit =
    dateLabel.text = s"Day: ${newState.time.day.value}, Year: ${newState.time.year.value}"

/**
 * Shows the current available DNA points
 */
case object DnaPointsCounter extends Label with UpdatableView:
  text = "DNA points: 0"
  padding = Insets(3)
  override def update(newState: SimulationState): Unit =
    this.text = s"DNA points: ${newState.disease.dnaPoints}"

/**
 * Displays the information about the infected, healthy and deceased people in the world
 */
case object InfectionRecap extends Label with UpdatableView:
  text = "Infected: 0 / Healthy: 0 \\ Deceased: 0"
  padding = Insets(3)
  override def update(newState: SimulationState): Unit =
    val infected = newState.world.nodes.values.map(_.infected).sum
    val deceased = newState.world.nodes.values.map(_.died).sum
    val healthy = newState.world.nodes.values.map(_.population).sum - infected
    this.text = s"Infected: $infected / " +
      s"Healthy: $healthy \\ " +
      s"Deceased: $deceased"