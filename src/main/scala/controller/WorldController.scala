// src/controller/WorldController.scala
package controller

import model.world.{Edge, Node, World}
import view.world.View
import view.event.*

class WorldController(private val world: World, private val view: View):

  def handle(event: ViewEvent): Unit = event match
    case AddNode(id, data) =>
      view.render(world.addNode(id, data))

    case RemoveNode(id) =>
      view.render(world.removeNode(id))

    case MovePeople(from, to, amount) =>
      view.render(world.movePeople(from, to, amount))

    case AddEdge(from, to, typology) =>
        view.render(world.addEdge(from, to, typology))

    case RemoveEdge(from, to, typology) =>
        view.render(world.removeEdge(from, to, typology))
    
    case StartWorldSimulation => 
      view.render(world)
    
    case PauseWorldSimulation => ???
    
    case UpdateNodePosition(_, _) => ???
    
    case UpdateNodeLabels(_, _) => ???
