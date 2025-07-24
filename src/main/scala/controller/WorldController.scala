// src/controller/WorldController.scala
package controller

import model.world.{Edge, Node, World}
import view.world.View
import view.event.*

class WorldController(private var world: World, private val view: View):

  def handle(event: ViewEvent): Unit = event match
    case StartSimulation =>
      println("Simulation started.")
      view.render(world)

    case PauseSimulation =>
      println("Simulation paused.")

    case MovePeople(from, to, amount) =>
      for
        fromNode <- world.nodes.get(from)
        toNode <- world.nodes.get(to)
      yield
        val fromUpdated = fromNode.decreasePopulation(amount)
        val toUpdated = toNode.increasePopulation(amount)
        world = world.modifyNodes(world.nodes.updated(from, fromUpdated).updated(to, toUpdated))
        view.render(world)

    case AddNode(id, data) =>
      world = world.modifyNodes(world.nodes + (id -> data))
      view.render(world)

    case RemoveNode(id) =>
      val updatedEdges = world.edges.filterNot { case (_, edge) => edge.connects(id) }
      val updatedNodes = world.nodes - id
      world = world.modifyNodes(updatedNodes).modifyEdges(updatedEdges)
      view.render(world)

    case AddEdge(from, to, typology) =>
      val edge = Edge(from, to, typology)
      val key = s"${from}_${to}_${typology.toString}"
      if !world.edges.contains(key) then
        world = world.modifyEdges(world.edges + (key -> edge))
        view.render(world)


    case RemoveEdge(from, to, typology) =>
      val key = s"${from}_${to}_${typology.toString}"
      if world.edges.contains(key) then
        world = world.modifyEdges(world.edges - key)
        view.render(world)


    case UpdateNodePosition(_, _) =>
      // This event is not handled in the current implementation.
      // It can be implemented to update node positions in the view if needed.
      println("UpdateNodePosition event received but not implemented.")

    case UpdateNodeLabels(_, _) =>
      // This event is not handled in the current implementation.
      // It can be implemented to update node labels in the view if needed.
      println("UpdateNodeLabels event received but not implemented.")
