package model.infection

import model.plague.Disease
import model.world.Node
import scala.util.Random

object InfectionStrategy:

  trait Infection:
    def calculateInfection(node: Node, disease: Disease): Node

  private case class FunctionalInfectionStrategy(
      adjustInfectivity: (Double, Node) => Double,
      spreadFunction: (Int, Double) => Int
  ) extends Infection:

    def calculateInfection(node: Node, disease: Disease): Node =
      val healthy = node.population - node.infected
      if healthy <= 0 || node.infected == 0 then node
      else
        val pressure      = node.infected.toDouble / node.population
        val infectivity   = adjustInfectivity(disease.infectivity, node)
        val probability   = infectivity * pressure
        val newInfections = spreadFunction(healthy, probability)
        node.applyInfection(newInfections)

  val StandardInfection: Infection = FunctionalInfectionStrategy(
    adjustInfectivity = (inf, _) => inf,
    spreadFunction = (healthy, prob) => (healthy * prob).toInt
  )

  def TemperatureAwareInfection(temp: Double): Infection =
    FunctionalInfectionStrategy(
      adjustInfectivity =
        (infectivity, _) => applyTemperaturePenalty(infectivity, temp),
      spreadFunction = (healthy, prob) => (healthy * prob).toInt
    )

  val ProbabilisticInfection: Infection = FunctionalInfectionStrategy(
    adjustInfectivity = (inf, _) => inf,
    spreadFunction =
      (healthy, prob) => (1 to healthy).count(_ => Random.nextDouble() < prob)
  )

  def ProbabilisticTemperatureInfection(temp: Double): Infection =
    FunctionalInfectionStrategy(
      adjustInfectivity =
        (infectivity, _) => applyTemperaturePenalty(infectivity, temp),
      spreadFunction =
        (healthy, prob) => (1 to healthy).count(_ => Random.nextDouble() < prob)
    )

  private def applyTemperaturePenalty(
      infectivity: Double,
      temperature: Double
  ): Double =
    val idealMinTemp     = 10.0
    val idealMaxTemp     = 30.0
    val penaltyPerDegree = 0.03

    temperature match
      case low if temperature < idealMinTemp =>
        val penalty = (idealMinTemp - temperature) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      case high if temperature > idealMaxTemp =>
        val penalty = (temperature - idealMaxTemp) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      case _ => infectivity
