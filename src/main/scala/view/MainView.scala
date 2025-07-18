package view

import controller.ViewController
import model.core.SimulationState
import model.world.WorldFactory
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane
import view.cure.CureProgressBar
import view.plague.PlagueView
import view.updatables.UpdatableView
import view.world.WorldViewFactory

class MainView extends BorderPane with UpdatableView:
  private val controller = ViewController(this)
  private val mapPane = WorldViewFactory.create(WorldFactory.mockWorld())
  private val plgPane = PlagueView()
  private val controlPane = ControlPane(controller)
  private val datePane = DatePane()
  private val progressBar = CureProgressBar()

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

  controlPane.center = progressBar

  center = mapPane
  bottom = controlPane
  top = datePane

  override def update(newState: SimulationState): Unit =
    plgPane.update(newState)
    datePane.update(newState)
    progressBar.update(newState)
    mapPane.update(newState)
end MainView

class DatePane extends BorderPane with UpdatableView:
  private val dateLabel = Label("date")
  left = dateLabel
  padding = Insets(10)

  override def update(newState: SimulationState): Unit =
    dateLabel.text = s"Day: ${newState.time.day.value}, Year: ${newState.time.year.value}"
