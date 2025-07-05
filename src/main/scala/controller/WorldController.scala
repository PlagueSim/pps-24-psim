package controller

import model.World.{Edge, Node, World}

class WorldController(initialWorld: World):
  private val world = initialWorld

  private var nodePositions: Map[String, (Double, Double)] =
    world.nodes.keys.map(id => id -> (0.0, 0.0)).toMap

  def getNodes: Map[String, Node] =
    world.nodes

  def getEdges: Set[Edge] =
    world.edges

  def getNodePositions: Map[String, (Double, Double)] =
    nodePositions

  def updateNodePosition(nodeId: String, x: Double, y: Double): Unit =
    nodePositions = nodePositions.updated(nodeId, (x, y))
