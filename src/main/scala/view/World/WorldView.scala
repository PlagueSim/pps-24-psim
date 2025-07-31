package view.world

import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.shape.Line
import scalafx.scene.Node as NodeVisual

class WorldView extends Pane with UpdatableView with VisualView:

  private var nodeViews: Map[String, NodeView] = Map.empty
  private var edgeViews: Map[String, Line] = Map.empty
  private var edges: Iterable[Edge] = List.empty
  private val layout: CircularLayout = CircularLayout()

  override def render(world: World): Unit =
    edges = world.edges.values
    val positionsMap = layout.computePositions(world.nodes.keySet.toSeq)

    nodeViews = NodeLayer.fromNodes(
      nodes = world.nodes,
      layout = id => positionsMap(id),
      onMoved = () => redrawEdges(edges)
    ).nodeViews

    edgeViews = EdgeLayer(
      edges = edges,
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
    children.removeAll(toRemove.toSeq: _*)
    children.addAll(toAdd.toSeq: _*)

  private def getNodesThatExistsAndChangedValues(nodes: Map[String, Node]): Map[String, NodeView] =
    nodes.collect {
      case (id, node) if nodeViews.get(id).exists(view =>
        view.population != node.population ||
          view.infected != node.infected ||
          view.died != node.died
      ) =>
        id -> nodeViews(id)
    }

  private def update(world: World): Unit =
    edges = world.edges.values
    val nodesChanged = getNodesThatExistsAndChangedValues(world.nodes)
    nodesChanged.foreach {
      case (id, view) =>
        view.updatePopulation(world.nodes(id).population)
        view.updateInfected(world.nodes(id).infected)
        view.updateDied(world.nodes(id).died)
    }
    val nodesThatDontExistAnymore = nodeViews.keySet -- world.nodes.keySet
    nodesThatDontExistAnymore.foreach { id =>
      nodeViews.get(id).foreach { view =>
        children.removeAll(view.visuals: _*)
        nodeViews -= id
      }
    }

    val nodesThatNeedsToBeCreated = world.nodes.keySet -- nodeViews.keySet
    nodesThatNeedsToBeCreated.foreach { id =>
      val node = world.nodes(id)
      val position = layout.computePositions(Seq(id)).getOrElse(id, (0.0, 0.0))
      val newNode = NodeLayer.createNode(id, node, position, () => redrawEdges(world.edges.values))
      nodeViews += (id -> newNode)
      children.addAll(newNode.visuals: _*)
    }

    redrawEdges(world.edges.values)





  override def update(newState: SimulationState): Unit =
    update(newState.world)

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
