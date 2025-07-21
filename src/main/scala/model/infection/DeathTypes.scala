package model.infection

import scala.util.Random

object DeathTypes:

  val StandardDeath: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      _.infected > 0,
      _.lethality,
      _.infected,
      (node, deaths) => node.updateDied(deaths),
      applyFunction = (infected, prob) => (infected * prob.value).toInt
    )

  val ProbabilisticDeath: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      _.infected > 0,
      _.lethality,
      _.infected,
      (node, deaths) => node.updateDied(deaths),
      applyFunction = (infected, prob) =>
        (1 to infected).count(_ => Random.nextDouble() < prob.value)
    )
