// src/view/console/ConsoleView.scala
package view.world

import model.core.SimulationState
import model.world.World
import scalafx.scene.control.Label
import view.event.{StartWorldSimulation, ViewEvent}

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

  override def update(newState: SimulationState): Unit =
    render(newState.world)

  override def getNodeView(id: String): Option[NodeView] = ???

  override def removeEdge(id: String): Unit = ???

  override def movePeople(from: String, to: String, amount: Int): Unit = ???

  override def removeNode(id: String): Unit = ???

  override def addNode(id: String, population: Int, infected: Int): Unit = ???

  override def addEdge(nodeA: String, nodeB: String, typology: String): Unit = ???

  override def updateNode(id: String, population: Int, infected: Int): Unit = ???
