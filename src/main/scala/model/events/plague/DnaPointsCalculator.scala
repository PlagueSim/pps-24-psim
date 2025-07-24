package model.events.plague

import model.world.Node
import scala.math.{floor, sqrt}

object DnaPointsCalculator:
  private type Infected = Int
  private type Deceased = Int
  private type World = Map[String, Node]

  private val NewNodeDnaMul = 5
  private val AffectedPopDnaRatio = 10

  private def extractDiff(prev: World, current: World): (Infected, Deceased) =
    val prevInfectedPop = prev.values.map(_.infected).sum
    val currentInfectedPop = current.values.map(_.infected).sum

    val prevDeceasedPop = prev.values.map(_.died).sum
    val currentDeceasedPop = current.values.map(_.died).sum

    (Math.max(currentInfectedPop - prevInfectedPop, 0), currentDeceasedPop - prevDeceasedPop)

  private def newInfectedNodes(prev: World, current: World): Int =
    val prevInfectedNodes = prev.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    val currentInfectedNodes = current.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    (currentInfectedNodes -- prevInfectedNodes).size


  def calculate(
                 prevNodes: World,
                 currentNodes: World
               ): Int =
    val (newInfectedPop, newDeceasedPop) = extractDiff(prevNodes, currentNodes)

    val redBubbles = newInfectedNodes(prevNodes, currentNodes)

    //todo: refine the calculation
    redBubbles * NewNodeDnaMul +
      floor(sqrt(newInfectedPop)).toInt / AffectedPopDnaRatio +
      floor(sqrt(newDeceasedPop)).toInt / AffectedPopDnaRatio
