package model.infection

import scala.util.Random

object DeathTypes:

  val StandardDeath: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      canApply = _.infected > 0,
      param = _.lethality,
      affected = _.infected,
      change = (node, deaths) => node.updateDied(deaths),
      applyFunction = (infected, prob) => (infected * prob.value).toInt
    )

  val ProbabilisticDeath: PopulationStrategy =
    PopulationStrategyBuilder.apply(
      canApply = _.infected > 0,
      param = _.lethality,
      affected = _.infected,
      change = (node, deaths) => node.updateDied(deaths),
      applyFunction = (infected, prob) =>
        (1 to infected).count(_ => Random.nextDouble() < prob.value)
    )
