package model.infection

import scala.util.Random

object DeathTypes:
  
  val StandardDeath: PopulationStrategy =
    PopulationStrategyBuilder.withProbability(
      _.lethality,
      _.infected,
      (node, deaths) => node.updateDied(deaths),
      applyFn = (infected, prob) => (infected * prob.value).toInt
    )

  val ProbabilisticDeath: PopulationStrategy =
    PopulationStrategyBuilder.withProbability(
      _.lethality,
      _.infected,
      (node, deaths) => node.updateDied(deaths),
      applyFn = (infected, prob) =>
        (1 to infected).count(_ => Random.nextDouble() < prob.value)
    )
