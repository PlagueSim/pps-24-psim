package view

import model.core.SimulationState
import view.updatables.UpdatableView

class ConsoleSimulationView extends UpdatableView:
  override def update(newState: SimulationState): Unit =
    println(s"\n[Day ${newState.time.day.value}, Year ${newState.time.year.value}]")
    newState.world.nodes.foreach { case (id, node) =>
      val infPct = f"${node.infectedPercentage() * 100}%.1f%%"
      println(f"$id -> Pop: ${node.population} Infected: ${node.infected} ($infPct)")
    }
