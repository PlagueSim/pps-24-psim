// src/view/console/ConsoleView.scala
package view.world

import model.world.World
import scalafx.scene.control.Label
import view.event.ViewEvent

class ConsoleView extends VisualView:

  private var eventHandler: ViewEvent => Unit = _ => ()

  override def render(world: World): Unit =
    println("\n=== World State ===")
    world.nodes.foreach { case (id, node) =>
      println(s"Node: $id → Population: ${node.population}, Infected: ${node.infected}")
    }
    println("Edges:")
    world.edges.foreach { case (id, edge) =>
      println(s"$id → ${edge.nodeA} ↔ ${edge.nodeB}")
    }

  override def handleEvent(event: ViewEvent): Unit =
    println(s"[ConsoleView] Event received: $event")

  override def setEventHandler(handler: ViewEvent => Unit): Unit =
    eventHandler = handler

  override def root = new Label("Console mode – no visual UI available.")