package model.infection

import model.plague.Disease
import model.world.Node
import scala.util.Random

object InfectionAndDeathPopulation:

  trait PopulationStrategy:
    def applyToPopulation(node: Node, disease: Disease): Node

  private case class FunctionalPopulationStrategy(
      canApply: Node => Boolean,
      extractParameter: Disease => Double,
      populationTypeTarget: Node => Int,
      adjustParameter: Double => Double,
      applyFunction: (Int, Double) => Int,
      applyChange: (Node, Int) => Node
  ) extends PopulationStrategy:
    override def applyToPopulation(
        node: Node,
        disease: Disease
    ): Node =
      if canApply(node) then
        val parameter         = extractParameter(disease)
        val adjustedParameter = adjustParameter(parameter)
        val basePopulation    = populationTypeTarget(node)
        val infectionPressure =
          if node.population equals 0 then 0.0
          else node.infected.toDouble / node.population
        val probability = adjustedParameter * infectionPressure
        val change      = applyFunction(basePopulation, probability)
        applyChange(node, change)
      else node

  object Infection:

    val StandardInfection: PopulationStrategy = FunctionalPopulationStrategy(
      canApply = n => n.population - n.infected >= 0 && (n.infected > 0),
      extractParameter = _.infectivity,
      populationTypeTarget = node => node.population - node.infected,
      adjustParameter = identity,
      applyFunction = (parameter, pressure) => (pressure * parameter).toInt,
      applyChange = (node, change) => node.applyInfection(change)
    )

    def StandardTemperatureAwareInfection(temp: Double): PopulationStrategy =
      FunctionalPopulationStrategy(
        canApply =
          node => node.population - node.infected > 0 && node.infected > 0,
        extractParameter = _.infectivity,
        populationTypeTarget = node => node.population - node.infected,
        adjustParameter =
          infectivity => applyTemperatureVariation(infectivity, temp),
        applyFunction = (healthy, probability) => (healthy * probability).toInt,
        applyChange = (node, newInfected) => node.applyInfection(newInfected)
      )

    val ProbabilisticInfection: PopulationStrategy =
      FunctionalPopulationStrategy(
        canApply =
          node => node.population - node.infected > 0 && node.infected > 0,
        extractParameter = _.infectivity,
        populationTypeTarget = node => node.population - node.infected,
        adjustParameter = identity,
        applyFunction = (healthy, probability) =>
          (1 to healthy).count(_ => Random.nextDouble() < probability),
        applyChange = (node, newInfected) => node.applyInfection(newInfected)
      )

    def ProbabilisticTemperatureInfection(temp: Double): PopulationStrategy =
      FunctionalPopulationStrategy(
        canApply =
          node => node.population - node.infected > 0 && node.infected > 0,
        extractParameter = _.infectivity,
        populationTypeTarget = node => node.population - node.infected,
        adjustParameter =
          infectivity => applyTemperatureVariation(infectivity, temp),
        applyFunction = (healthy, probability) =>
          (1 to healthy).count(_ => Random.nextDouble() < probability),
        applyChange = (node, newInfected) => node.applyInfection(newInfected)
      )

    private def applyTemperatureVariation(
        infectivity: Double,
        temperature: Double
    ): Double =
      val idealMinTemp     = 10.0
      val idealMaxTemp     = 30.0
      val penaltyPerDegree = 0.03

      temperature match
        case _ if temperature < idealMinTemp =>
          val penalty = (idealMinTemp - temperature) * penaltyPerDegree
          (infectivity * (1.0 - penalty)).max(0)
        case _ if temperature > idealMaxTemp =>
          val penalty = (temperature - idealMaxTemp) * penaltyPerDegree
          (infectivity * (1.0 - penalty)).max(0)
        case _ => infectivity

    object Death:
      val StandardDeath: PopulationStrategy = FunctionalPopulationStrategy(
        canApply = node => node.infected > 0,
        extractParameter = _.lethality / 100.0,
        populationTypeTarget = _.infected,
        adjustParameter = identity,
        applyFunction = (infected, lethality) => (infected * lethality).toInt,
        applyChange = (node, deaths) => node.decreasePopulation(deaths)
      )

      val ProbabilisticDeath: PopulationStrategy =
        FunctionalPopulationStrategy(
          canApply = node => node.infected > 0,
          extractParameter = _.lethality / 100.0,
          populationTypeTarget = _.infected,
          adjustParameter = identity,
          applyFunction = (infected, lethality) =>
            (1 to infected).count(_ => Random.nextDouble() < lethality),
          applyChange = (node, deaths) => node.decreasePopulation(deaths)
        )
