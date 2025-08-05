package model.infection

import model.plague.Disease
import model.world.Node
import org.apache.commons.math3.distribution.BinomialDistribution

/** Object containing all the different types of death logics */
object DeathTypes:
  
  private val STANDARD_CAN_APPLY: (Node, Disease) => Boolean = (n, d) => n.infected > 0 && d.lethality > 0.0

  val StandardDeath: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) => (infected * prob.value).ceil.toInt,
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )

  val ProbabilisticDeath: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) =>
        val binomial = new BinomialDistribution(infected, prob.value)
        binomial.sample(),
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )
