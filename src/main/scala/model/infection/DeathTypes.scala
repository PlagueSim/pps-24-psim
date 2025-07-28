package model.infection

import model.world.Node
import org.apache.commons.math3.distribution.BinomialDistribution

/** Object containing all the different types of death logics */
object DeathTypes:
  
  private val STANDARD_CAN_APPLY: Node => Boolean = _.infected > 0

  val StandardDeath: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) => (infected * prob.value).toInt,
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )

  val ProbabilisticDeath: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) =>
        val binomial = new BinomialDistribution(infected, prob.value)
        binomial.sample(),
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )
