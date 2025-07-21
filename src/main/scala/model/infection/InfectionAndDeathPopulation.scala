package model.infection

import model.plague.Disease
import model.world.Node
import scala.util.Random
import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster

object InfectionAndDeathPopulation:

  private case class FunctionalPopulationStrategy(
      canApply: Node => Boolean,
      extractParameter: Disease => Double,
      populationTypeTarget: Node => Int,
      adjustParameter: Double => Probability,
      applyFunction: (Int, Probability) => Int,
      applyChange: (Node, Int) => Node
  ) extends PopulationStrategy:
    override def applyToPopulation(node: Node, disease: Disease): Node =
      if canApply(node) then
        lazy val rawParam       = extractParameter(disease)
        lazy val probability    = adjustParameter(rawParam)
        lazy val basePopulation = populationTypeTarget(node)
        val change              = applyFunction(basePopulation, probability)
        applyChange(node, change)
      else node

  private object PopulationStrategyBuilder:
    def withProbability(
        param: Disease => Double,
        affected: Node => Int,
        change: (Node, Int) => Node,
        adjust: Double => Probability = Probability.fromPercentage,
        applyFn: (Int, Probability) => Int
    ): PopulationStrategy =
      FunctionalPopulationStrategy(
        canApply = node => affected(node) > 0,
        extractParameter = param,
        populationTypeTarget = affected,
        adjustParameter = adjust,
        applyFunction = applyFn,
        applyChange = change
      )

  object Infection:

    val StandardInfection: PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.infectivity,
        node => node.population - node.infected,
        (node, infected) => node.applyInfection(infected),
        applyFn = (healthy, prob) => (healthy * prob.value).toInt
      )

    def WithTemperature(temp: Double)(using
        adjuster: TemperatureAdjuster
    ): PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.infectivity,
        node => node.population - node.infected,
        (node, infected) => node.applyInfection(infected),
        adjust = p =>
          Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
        applyFn = (healthy, prob) => (healthy * prob.value).toInt
      )

    val Probabilistic: PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.infectivity,
        node => node.population - node.infected,
        (node, infected) => node.applyInfection(infected),
        applyFn = (healthy, prob) =>
          (1 to healthy).count(_ => Random.nextDouble() < prob.value)
      )

    def ProbabilisticWithTemperature(temp: Double)(using
        adjuster: TemperatureAdjuster
    ): PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.infectivity,
        node => node.population - node.infected,
        (node, infected) => node.applyInfection(infected),
        adjust = p =>
          Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
        applyFn = (healthy, prob) =>
          (1 to healthy).count(_ => Random.nextDouble() < prob.value)
      )

  object Death:

    val StandardDeath: PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.lethality,
        _.infected,
        (node, deaths) => node.updateDied(deaths),
        applyFn = (infected, prob) => (infected * prob.value).toInt
      )

    val ProbabilisticDeath: PopulationStrategy =
      PopulationStrategyBuilder.withProbability(
        _.lethality,
        _.infected,
        (node, deaths) => node.updateDied(deaths),
        applyFn = (infected, prob) =>
          (1 to infected).count(_ => Random.nextDouble() < prob.value)
      )
