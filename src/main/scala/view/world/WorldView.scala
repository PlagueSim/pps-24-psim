package view.world

import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.shape.Line
import scalafx.scene.Node as NodeVisual
import model.world.Types.*
class WorldView extends Pane with UpdatableWorldView:

  private var nodeViews: Map[NodeId, NodeView] = Map.empty
  private var edgeViews: Map[EdgeId, Line] = Map.empty
  private var edges: Iterable[Edge] = List.empty
  private val layout: CircularLayout = CircularLayout()

  /**
   * Renders the world view by computing positions for nodes and creating visual representations.
   * @param world The current state of the world containing nodes and edges.
   * initializes the node and edge views based on the world state.
   * computes the positions of nodes using the layout strategy and creates NodeViews
   * for each node.
   * It also creates EdgeLines for each edge based on the current node positions.
   * This method is called to render the initial state of the world.
   * */
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


  /**
   *
   * @param world
   * Updates the world view with the latest state of the world.
   * This method checks for changes in node populations, infected counts, and deaths,
   * and updates the visual representations accordingly.
   * It also handles the addition and removal of nodes
   * and edges based on the current world state.
   *
   */
  private def update(world: World): Unit =
    val totPopulation = world.nodes.values.map(_.population).sum
    edges = world.edges.values
    val nodesChanged = getNodesThatExistsAndChangedValues(world.nodes)
    nodesChanged.foreach {
      case (id, view) =>
        view.updatePopulation(world.nodes(id).population)
        view.updateInfected(world.nodes(id).infected)
        view.updateDied(world.nodes(id).died)
        view.updateBackground()
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

  private def getNodesThatExistsAndChangedValues(nodes: Map[NodeId, Node]): Map[String, NodeView] =
    nodes.collect {
      case (id, node) if nodeViews.get(id).exists(view =>
        view.population != node.population ||
          view.infected != node.infected ||
          view.died != node.died
      ) =>
        id -> nodeViews(id)
    }

