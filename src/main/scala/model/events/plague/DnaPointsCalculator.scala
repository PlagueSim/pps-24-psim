package model.events.plague

import model.world.Node
import scala.math.{floor, sqrt}

object DnaPointsCalculator:

  private val NewNodeDnaMul = 5
  private val AffectedPopDnaRatio = 10

  def calculate(
                 prevNodes: Map[String, Node],
                 currentNodes: Map[String, Node]
               ): Int =
    val prevInfectedPop = prevNodes.values.map(_.infected).sum
    val currentInfectedPop = currentNodes.values.map(_.infected).sum
    val newInfectedPop = Math.max(currentInfectedPop - prevInfectedPop, 0)

    val prevDeceasedPop = prevNodes.values.map(_.died).sum
    val currentDeceasedPop = currentNodes.values.map(_.died).sum
    val newDeceasedPop = currentDeceasedPop - prevDeceasedPop

    val prevInfectedNodes = prevNodes.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    val currentInfectedNodes = currentNodes.collect {
      case (name, node) if node.infected > 0 => name
    }.toSet

    val newInfectedNodes = currentInfectedNodes -- prevInfectedNodes

    newInfectedNodes.size * NewNodeDnaMul +
      floor(sqrt(newInfectedPop)).toInt / AffectedPopDnaRatio +
      floor(sqrt(newDeceasedPop)).toInt / AffectedPopDnaRatio
