package model.infection

import model.plague.Disease
import model.world.Node
import scala.util.Random

object InfectionStrategy:

  trait Infection:
    def calculateInfection(node: Node, disease: Disease): Node

  case class StandardInfection() extends Infection:
    def calculateInfection(node: Node, disease: Disease): Node =
      (node.infected, node.population) match
        case (infected, population) if infected equals population => node
        case _                                                    =>
          node.applyInfection(
            calculateNewInfected(
              node.infected,
              node.population,
              disease.infectivity
            )
          )

    private def calculateNewInfected(
        infected: Int,
        population: Int,
        infectivity: Double
    ): Int =
      if population equals 0 then 0
      else
        val healthy           = population - infected
        val infectionPressure = infected.toDouble / population
        val spreadRate        = infectivity * infectionPressure
        (healthy * spreadRate).toInt

  case class TemperatureAwareInfection(temperature: Double) extends Infection:
    def calculateInfection(node: Node, disease: Disease): Node =
      val healthy = node.population - node.infected
      if healthy <= 0 || node.infected == 0 then node
      else
        val adjustedInfectivity =
          applyTemperaturePenalty(disease.infectivity, temperature)
        val newInfections =
          calc(healthy, node.infected, node.population, adjustedInfectivity)
        node.applyInfection(newInfections)

    private def calc(
        healthy: Int,
        infected: Int,
        population: Int,
        infectivity: Double
    ): Int =
      val infectionPressure = infected.toDouble / population
      val spreadRate        = infectivity * infectionPressure
      (healthy * spreadRate).toInt

    private def applyTemperaturePenalty(
        infectivity: Double,
        temperature: Double
    ): Double =
      val idealMin         = 10.0
      val idealMax         = 30.0
      val penaltyPerDegree = 0.03

      if temperature < idealMin then
        val penalty = (idealMin - temperature) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      else if temperature > idealMax then
        val penalty = (temperature - idealMax) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      else infectivity

  case class ProbabilisticInfection() extends Infection:
    def calculateInfection(node: Node, disease: Disease): Node =
      val healthy = node.population - node.infected
      if healthy <= 0 || node.infected == 0 then node
      else
        val probability   = disease.infectivity * infectionPressure(node)
        val newInfections = simulateInfections(healthy, probability)
        node.applyInfection(newInfections)

    private def infectionPressure(node: Node): Double =
      node.infected.toDouble / node.population

    private def simulateInfections(healthy: Int, probability: Double): Int =
      (1 to healthy).count(_ => Random.nextDouble() < probability)

  case class ProbabilisticTemperatureInfection(temperature: Double)
      extends Infection:
    def calculateInfection(node: Node, disease: Disease): Node =
      val healthy = node.population - node.infected
      if healthy <= 0 || node.infected == 0 then node
      else
        val adjustedInfectivity =
          applyTemperaturePenalty(disease.infectivity, temperature)
        val probability   = adjustedInfectivity * infectionPressure(node)
        val newInfections = simulateInfections(healthy, probability)
        node.applyInfection(newInfections)

    private def infectionPressure(node: Node): Double =
      node.infected.toDouble / node.population

    private def simulateInfections(healthy: Int, probability: Double): Int =
      (1 to healthy).count(_ => Random.nextDouble() < probability)

    private def applyTemperaturePenalty(
        infectivity: Double,
        temperature: Double
    ): Double =
      val idealMin         = 10.0
      val idealMax         = 30.0
      val penaltyPerDegree = 0.03

      if temperature < idealMin then
        val penalty = (idealMin - temperature) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      else if temperature > idealMax then
        val penalty = (temperature - idealMax) * penaltyPerDegree
        (infectivity * (1.0 - penalty)).max(0)
      else infectivity
