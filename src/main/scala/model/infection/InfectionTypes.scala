package model.infection

import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import model.world.Node
import org.apache.commons.math3.distribution.{
  BinomialDistribution,
  HypergeometricDistribution
}

/** Object containing all the infection types */
object InfectionTypes:

  private val STANDARD_CAN_APPLY: Node => Boolean =
    n => n.infected > 0 && n.population - n.infected > 0

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

  /** Advanced probabilistic infection logic that allows for a variable number
    * of affected individuals
    */
  def AdvancedProbabilistic(affectable: Int): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => {
        val totalAffected =
          try {
            Math.multiplyExact(node.infected, affectable)
          } catch {
            case e: ArithmeticException =>
              println(
                s"Overflow occurred while calculating totalAffected: ${e.getMessage}"
              )
              Int.MaxValue // Fallback to a safe value
          }
        val hyp = new HypergeometricDistribution(
          node.population,
          node.population - node.infected,
          totalAffected
        )
        hyp.sample()
      },
      changeCalculator = (healthy, prob) =>
        new BinomialDistribution(healthy, prob.value).sample(),
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )
