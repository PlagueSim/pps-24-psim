package model.infection

import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster

import scala.util.Random

object InfectionTypes:
  val StandardInfection: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      n => n.infected > 0 && n.population - n.infected > 0,
      _.infectivity,
      node => node.population - node.infected,
      (node, infected) => node.applyInfection(infected),
      applyFunction = (healthy, prob) => (healthy * prob.value).toInt
    )

  def WithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationStrategy =
    PopulationStrategyBuilder.apply(
      n => n.infected > 0 && n.population - n.infected > 0,
      _.infectivity,
      node => node.population - node.infected,
      (node, infected) => node.applyInfection(infected),
      adjust =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      applyFunction = (healthy, prob) => (healthy * prob.value).toInt
    )

  val Probabilistic: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      n => n.infected > 0 && n.population - n.infected > 0,
      _.infectivity,
      node => node.population - node.infected,
      (node, infected) => node.applyInfection(infected),
      applyFunction = (healthy, prob) =>
        (1 to healthy).count(_ => Random.nextDouble() < prob.value)
    )

  def ProbabilisticWithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationStrategy =
    PopulationStrategyBuilder.apply(
      n => n.infected > 0 && n.population - n.infected > 0,
      _.infectivity,
      node => node.population - node.infected,
      (node, infected) => node.applyInfection(infected),
      adjust =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      applyFunction = (healthy, prob) =>
        (1 to healthy).count(_ => Random.nextDouble() < prob.value)
    )
