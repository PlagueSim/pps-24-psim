package model.infection

import model.plague.Disease
import model.world.Node
import scala.util.Random
import model.infection.InfectionAndDeathPopulation.Probability.value

object InfectionAndDeathPopulation:

  opaque type Probability = Double

  object Probability:
    def fromPercentage(p: Double): Probability   = (p / 100.0).max(0).min(1)
    extension (p: Probability) def value: Double = p

  trait Rounding:
    def round(value: Double): Int
  object Rounding:
    given floor: Rounding with
      def round(value: Double): Int = math.floor(value).toInt

  trait TemperatureAdjuster:
    def adjustForTemperature(value: Double, temperature: Double): Double

  given defaultTemperatureAdjuster: TemperatureAdjuster with
    private val idealMin = 10.0
    private val idealMax = 30.0
    private val penalty  = 0.03

    def adjustForTemperature(
        value: Double,
        temp: Double
    ): Double =
      temp match
        case low if temp < idealMin => value * (1 - (idealMin - temp) * penalty)
        case high if temp > idealMax =>
          value * (1 - (temp - idealMax) * penalty)
        case _ => value

  trait PopulationStrategy:
    def applyToPopulation(node: Node, disease: Disease): Node

  private case class FunctionalPopulationStrategy(
      canApply: Node => Boolean,
      extractParameter: Disease => Double,
      populationTypeTarget: Node => Int,
      adjustParameter: Double => Probability,
      applyFunction: (Int, Probability) => Int,
      applyChange: (Node, Int) => Node
  ) extends PopulationStrategy:
    override def applyToPopulation(node: Node, disease: Disease): Node =
      if canApply(node) then
        lazy val rawParam       = extractParameter(disease)
        lazy val probability    = adjustParameter(rawParam)
        lazy val basePopulation = populationTypeTarget(node)
        val change              = applyFunction(basePopulation, probability)
        applyChange(node, change)
      else node

  private object PopulationStrategyBuilder:
    def withProbability(
        param: Disease => Double,
        affected: Node => Int,
        change: (Node, Int) => Node,
        adjust: Double => Probability = Probability.fromPercentage,
        applyFn: (Int, Probability) => Int
    ): PopulationStrategy =
      FunctionalPopulationStrategy(
        canApply = node => affected(node) > 0,
        extractParameter = param,
        populationTypeTarget = affected,
        adjustParameter = adjust,
        applyFunction = applyFn,
        applyChange = change
      )

  object Infection:

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
        adjust = p =>
          Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
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
        adjust = p =>
          Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
        applyFn = (healthy, prob) =>
          (1 to healthy).count(_ => Random.nextDouble() < prob.value)
      )

  object Death:

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
