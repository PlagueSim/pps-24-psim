package model.infection

import model.infection.TemperatureAdjuster.TemperatureAdjuster
import model.infection.Probability.Probability
import scala.util.Random

object InfectionTypes:
  import PopulationStrategyBuilder.withProbability
  
  
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
      adjust =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
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
      adjust =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      applyFn = (healthy, prob) =>
        (1 to healthy).count(_ => Random.nextDouble() < prob.value)
    )
