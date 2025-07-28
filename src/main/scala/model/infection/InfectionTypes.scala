package model.infection

import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import model.world.Node

import scala.util.Random

/** Object containing all the infection types */
object InfectionTypes:
  
  private val STANDARD_CAN_APPLY: Node => Boolean =
    n => n.infected > 0 && n.population - n.infected > 0
  
  val StandardInfection: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Standard infection logic with temperature */
  def WithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  val ProbabilisticInfection: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) =>
        (1 to healthy).count(_ => Random.nextDouble() < prob.value),
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Probabilistic infection logic with temperature */
  def ProbabilisticInfectionWithTemperature(temp: Double)(using
                                                          adjuster: TemperatureAdjuster
  ): PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) =>
        (1 to healthy).count(_ => Random.nextDouble() < prob.value),
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )
