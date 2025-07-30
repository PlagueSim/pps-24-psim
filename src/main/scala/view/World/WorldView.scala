package view.world

import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane
import view.event.ViewEvent
import view.updatables.UpdatableView
import javafx.scene.shape.Line
import scalafx.scene.Node as NodeVisual

class WorldView extends Pane with UpdatableView with VisualView:

  private var eventHandler: ViewEvent => Unit = _ => ()
  private var nodeViews: Map[String, NodeView] = Map.empty
  private var edgeViews: Map[String, Line] = Map.empty
  private val layout: CircularLayout = CircularLayout()

  private var worldRenderer: Option[WorldRenderer] = None
  private var currentWorld: Option[World] = None

  override def setEventHandler(handler: ViewEvent => Unit): Unit =
    this.eventHandler = handler

  override def render(world: World): Unit =
    currentWorld = Some(world)

    val positionsMap = layout.computePositions(world.nodes.keySet.toSeq)

    nodeViews = NodeLayer.fromNodes(
      nodes = world.nodes,
      layout = id => positionsMap(id),
      onMoved = () => redrawEdges(world.edges.values)
    ).nodeViews

    edgeViews = EdgeLayer(
      edges = world.edges.values,
      nodePositions = nodeViews.view.mapValues(nv => LivePosition(nv.position)).toMap
    ).edgeLines

    val visuals = WorldRenderer.render(nodeViews, edgeViews, layout)
    children.setAll(visuals: _*)

  private def redrawEdges(updatedEdges: Iterable[Edge]): Unit =
    val (newEdgeMap, toAdd, toRemove) =
      EdgeUpdater.update(
        edgeViews,
        updatedEdges,
        nodeViews.view.mapValues(nv => LivePosition(nv.position)).toMap
      )
    edgeViews = newEdgeMap
    children.removeAll(toRemove.toSeq*)
    children.addAll(toAdd.toSeq*)

  private def getNodesChanged(nodes: Map[String, Node]): Map[String, NodeView] =
    nodeViews.filter { case (id, view) =>
      nodes.get(id) match
        case Some(node) => view.population != node.population ||
                          view.infected != node.infected ||
                          view.died != node.died
        case None => true
    }

  private def update(world: World): Unit =
    val nodesChanged = getNodesChanged(world.nodes)
    nodesChanged.foreach {
      case (id, view) =>
        view.updatePopulation(world.nodes(id).population)
        view.updateInfected(world.nodes(id).infected)
        view.updateDied(world.nodes(id).died)
    }
    
    

  override def update(newState: SimulationState): Unit =
    update(newState.world)

  override def handleEvent(event: ViewEvent): Unit =
    eventHandler(event)

  override def root: NodeVisual = this

  override def getNodeView(id: String): Option[NodeView] =
    nodeViews.get(id)

  override def removeEdge(id: String): Unit =
    edgeViews.get(id).foreach { line =>
      children.remove(line)
      edgeViews -= id
    }

  override def movePeople(from: String, to: String, amount: Int): Unit =
    nodeViews.get(from).foreach(x => x.updatePopulation(x.population - amount))
    nodeViews.get(to).foreach(x => x.updatePopulation(x.population + amount))

  override def removeNode(id: String): Unit = ???
  override def addNode(id: String, population: Int, infected: Int): Unit = ???

  override def addEdge(nodeA: String, nodeB: String, typology: String): Unit = ???

  override def updateNode(id: String, population: Int, infected: Int): Unit =
    nodeViews.get(id).foreach { view =>
      view.updatePopulation(population)
      view.updateInfected(infected)
    }
