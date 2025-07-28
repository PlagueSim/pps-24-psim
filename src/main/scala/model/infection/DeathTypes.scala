package model.infection

import scala.util.Random

/** Object containing all the different types of death logics */
object DeathTypes:

  val StandardDeath: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = _.infected > 0,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) => (infected * prob.value).toInt,
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )

  val ProbabilisticDeath: PopulationEffect =
    PopulationEffectBuilder.apply(
      canApply = _.infected > 0,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) =>
        (1 to infected).count(_ => Random.nextDouble() < prob.value),
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )
