// src/controller/WorldController.scala
package controller

import model.world.{Edge, Node, World}
import view.world.View
import view.event.*

class WorldController(private val world: World, private val view: View):

  def handle(event: ViewEvent): Unit = event match
    case AddNode(id, data) =>
      world.addNode(id, data)

    case RemoveNode(id) =>
      view.removeNode(id)

    case MovePeople(from, to, amount) =>
      view.movePeople(from, to, amount)

    case AddEdge(from, to, typology) =>
      view.addEdge(from, to, typology.toString)

    case RemoveEdge(from, to, typology) =>
      view.removeEdge(from+"-"+to+"-"+"-"+typology)
    
    case StartWorldSimulation => 
      view.render(world)

    case UpdateNodeLabels(id, labels) => ???

    case PauseWorldSimulation => ???
    
    case UpdateNodePosition(_, _) => ???

