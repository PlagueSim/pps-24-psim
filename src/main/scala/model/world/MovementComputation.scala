package model.world

import model.events.movementEvent.MovementStrategyLogic
import Types.*
import org.apache.commons.math3.distribution.HypergeometricDistribution
object MovementComputation:

  case class PeopleMovement(from: NodeId, to: NodeId, amount: Int)
  case class MovementResult(updatedNodes: Map[NodeId, Node], moves: List[PeopleMovement])

  /**
   * Computes all movements of people in the world using specified strategies.
   * For each strategy, it calculates proposed movements and, using a hypergeometric distribution,
   * samples how many infected individuals move along each edge.
   * The movements are then applied to update node populations and infection counts in a
   * purely functional manner.
   *
   * @param world the current state with nodes, edges, and movement strategies
   * @param rng a random number generator for probabilistic decisions and hypergeometric sampling
   * @return a MovementResult containing updated nodes and the list of all PeopleMovement events generated
   */
  
  def computeAllMovements(world: World, rng: scala.util.Random): MovementResult =
    world.movements.foldLeft(MovementResult(world.nodes, List.empty)) {
      case (MovementResult(currentNodes, accMoves), (strategy, percent)) =>
        val newMoves = MovementStrategyLogic.compute(world, strategy, percent, rng)
        val updatedNodes = applyMovements(world.modifyNodes(currentNodes), newMoves).nodes
        MovementResult(updatedNodes, accMoves ++ newMoves)
    }

  /*
   * Applies a list of movements to the world.
   * This method updates the population and infection counts of nodes based on the movements.
   */
  private def applyMovements(world: World, movements: Iterable[PeopleMovement]): World = {
    val updatedNodes = movements.foldLeft(world.nodes):
      case (nodesAcc, move) => updateNodesWithMovement(nodesAcc, move)
    World(
      nodes = updatedNodes,
      edges = world.edges,
      movements = world.movements
    )
  }

  private def updateNodesWithMovement(
                                       nodes: Map[NodeId, Node],
                                       movement: PeopleMovement
                                     ): Map[NodeId, Node] =
    val PeopleMovement(from, to, amount) = movement

    val fromNode = nodes(from)
    if fromNode.population <= 0 || amount <= 0 then return nodes

    val infectedMoving = sampleInfected(fromNode, amount.min(fromNode.population))

    val updatedFrom = fromNode
      .decreasePopulation(amount)
      .decreaseInfection(infectedMoving)

    val updatedTo = nodes(to)
      .increasePopulation(amount)
      .increaseInfection(infectedMoving)

    nodes.updated(from, updatedFrom)
      .updated(to, updatedTo)

  private def sampleInfected(node: Node, amount: Int): Int =
    val hgd = new HypergeometricDistribution(
      node.population,
      node.infected,
      amount
    )
    hgd.sample()
