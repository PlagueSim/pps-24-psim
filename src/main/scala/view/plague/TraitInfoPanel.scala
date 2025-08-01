package view.plague

import model.core.SimulationState
import model.events.plague.DiseaseEvents.*
import model.events.DiseaseEventBuffer
import model.events.DiseaseEventBuffer.newEvent
import scalafx.scene.layout.{BorderPane, VBox}
import model.plague.Trait
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.text.Font
import view.updatables.UpdatableView
import view.util.StdButton

/**
 * The panel that shows The selected [[Trait]] information
 *
 * @param tr The [[Trait]] selected
 */
class TraitInfoPanel(tr: Trait) extends BorderPane with UpdatableView:
  private val nameLabel = new Label(s"${tr.name}"):
    font = Font(18)
    padding = Insets(10)

  private val evolveButton = StdButton("Evolve"):
    newEvent(Evolution(tr))

  private val involveButton = StdButton("Involve"):
    newEvent(Involution(tr))

  private def statLabel(name: String, value: Double): Option[Label] = value match
    case v if v != 0.0 => Some(Label(s"$name: ${"%.2f".format(value)}"))
    case _ => None

  private def effectivenessLabels(effMap: Map[Any, Double]): Seq[Label] = effMap match
    case map if map.nonEmpty => Label("Effectiveness:") +:
      map.toSeq.map((k, v) => Label(s" - $k: ${"%.2f".format(v)}"))
    case _ => Seq.empty

  private val statsLabels: Seq[Label] =
    statLabel("Cost", tr.stats.cost).toSeq ++
      statLabel("Infectivity", tr.stats.infectivity).toSeq ++
      statLabel("Severity", tr.stats.severity).toSeq ++
      statLabel("Lethality", tr.stats.lethality).toSeq ++
      statLabel("Mutation Chance", tr.stats.mutationChance).toSeq ++
      statLabel("Cure Slowdown", tr.stats.cureSlowdown).toSeq ++
      statLabel("Cure Reset", tr.stats.cureReset).toSeq ++
      effectivenessLabels(tr.stats.effectiveness)

  private val infoPanel = new VBox():
    spacing = 5
    padding = Insets(10)
    children = statsLabels

  top = nameLabel
  center = infoPanel
  bottom = evolveButton

  override def update(newState: SimulationState): Unit = newState.disease match
    case d if d.traits.contains(tr) => this.bottom = involveButton
    case _ => this.bottom = evolveButton


