package model.infection

import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import model.plague.Disease
import model.world.Node
import org.apache.commons.math3.distribution.{
  BinomialDistribution,
  HypergeometricDistribution
}
import org.apache.commons.math3.util.FastMath.pow

/** Object containing all the infection types */
object InfectionTypes:

  private val STANDARD_CAN_APPLY: (Node, Disease) => Boolean =
    (n, d) => n.infected > 0 && n.population - n.infected > 0 && d.infectivity > 0.0

  val StandardInfection: PopulationEffect =
    PopulationEffectComposer.apply(
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
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  val ProbabilisticInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) =>
        val binomial = new BinomialDistribution(healthy, prob.value)
        binomial.sample()
      ,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Probabilistic infection logic with temperature */
  def ProbabilisticInfectionWithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) =>
        val binomial = new BinomialDistribution(healthy, prob.value)
        binomial.sample()
      ,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  val SIRLogic: PopulationEffect =
    PopulationEffectComposer.apply[(Int,Int)](
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => {
        (node.population, node.infected)
      },
      changeCalculator = (populationAndInfected, prob) => {
        if prob.value <= 0 then 0
        else
          val (population, infected) = populationAndInfected
          val healthy = population - infected
          val beta = prob.value
          val delta = beta * infected.toDouble * healthy.toDouble / population.toDouble
          math.max(1, delta).toInt
      },
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )