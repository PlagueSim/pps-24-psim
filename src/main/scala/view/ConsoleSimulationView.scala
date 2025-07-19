package view

import model.core.SimulationState
import view.updatables.UpdatableView

class ConsoleSimulationView extends UpdatableView:

  override def update(newState: SimulationState): Unit =
    println("\n====================")
    println(s"📅 Simulation Time: Day ${newState.time.day.value}, Year ${newState.time.year.value}")
    println("====================")
    println("🌍 Node Status:")

    newState.world.nodes.toSeq.sortBy(_._1).foreach { case (id, node) =>
      val infectedPct = node.infectedPercentage() * 100
      println(f"- Node $id%s:")
      println(f"    Population       : ${node.population}%d")
      println(f"    Infected         : ${node.infected}%d")
      println(f"    Infected Percent : $infectedPct%.2f%%")
    }

    println("====================\n")
