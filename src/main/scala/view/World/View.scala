package view.world

import model.world.World
import view.updatables.UpdatableView


trait View extends UpdatableView {
  def getNodeView(id: String): Option[NodeView]
  def removeEdge(id: String): Unit
  def movePeople(from: String, to: String,  amount: Int): Unit
  def removeNode(id: String): Unit
  def addNode(id: String, population: Int, infected: Int): Unit
  def addEdge(nodeA: String, nodeB: String, typology: String): Unit
  def updateNode(id: String, population: Int, infected: Int): Unit
  
  def render(world: World): Unit
}