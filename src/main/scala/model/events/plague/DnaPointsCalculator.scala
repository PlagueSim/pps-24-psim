package model.events.plague

import model.world.Node
import scala.math.{floor, sqrt}

object DnaPointsCalculator:
  private type Infected = Int
  private type Deceased = Int
  private type World = Map[String, Node]

  private val NewNodeDnaMul = 5
  private val AffectedPopDnaRatio = 5
  private val Factor = 10000

  /**
   * Computes the difference in infected and deceased populations between two snapshots of the world.
   *
   * @param prev the previous world state
   * @param current the current world state
   * @return a tuple containing:
   *         - the difference in infected population
   *         - the difference in deceased population
   */
  private def extractDiff(prev: World, current: World): (Infected, Deceased) =
    val prevInfectedPop = prev.values.map(_.infected).sum
    val currentInfectedPop = current.values.map(_.infected).sum

    val prevDeceasedPop = prev.values.map(_.died).sum
    val currentDeceasedPop = current.values.map(_.died).sum

    (Math.max(currentInfectedPop - prevInfectedPop, 0), currentDeceasedPop - prevDeceasedPop)

  /**
   * Calculates the number of nodes that have transitioned from uninfected to infected.
   *
   * @param prev the previous world state
   * @param current the current world state
   * @return the count of newly infected nodes
   */
  private def newInfectedNodes(prev: World, current: World): Int =
    val prevInfectedNodes = prev.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    val currentInfectedNodes = current.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    (currentInfectedNodes -- prevInfectedNodes).size

  /**
   * Computes the total DNA points to assign based on the changes in the world state.
   *
   * @param infected the amount of newly infected population
   * @param deceased the amount of newly deceased population
   * @param redBubbles the number of newly infected nodes
   * @param currentWorld the current world state
   * @return the total DNA points to assign
   */
  private def assignDnaPoints(infected: Infected, deceased: Deceased, redBubbles: Int, currentWorld: World): Int =
    val totalPopulation = currentWorld.values.map(n => n.population + n.died).sum
    if totalPopulation == 0 then 0
    else
      val infectedScore = floor(sqrt((infected.toDouble / totalPopulation) * Factor)).toInt / AffectedPopDnaRatio
      val deceasedScore = floor(sqrt((deceased.toDouble / totalPopulation) * Factor)).toInt / AffectedPopDnaRatio
      redBubbles * NewNodeDnaMul + infectedScore + deceasedScore

  /**
   * Calculates the amount of DNA points to be awarded based on the difference
   * between a previous and current world state.
   *
   * @param prevNodes the world state before the update
   * @param currentNodes the world state after the update
   * @return the number of DNA points earned
   */
  def calculate(
                 prevNodes: World,
                 currentNodes: World
               ): Int =
    val (newInfectedPop, newDeceasedPop) = extractDiff(prevNodes, currentNodes)
    val redBubbles = newInfectedNodes(prevNodes, currentNodes)

    assignDnaPoints(newInfectedPop, newDeceasedPop, redBubbles, currentNodes)
