package model.infection

import Probability.Probability
import model.world.Node
import model.plague.Disease

private[infection] object PopulationStrategyBuilder:
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

  def apply(
             canApply: Node => Boolean,
             param: Disease => Double,
             affected: Node => Int,
             adjust: Double => Probability = Probability.fromPercentage,
             applyFunction: (Int, Probability) => Int,
             change: (Node, Int) => Node
  ): PopulationStrategy =
    FunctionalPopulationStrategy(
      canApply = canApply,
      extractParameter = param,
      populationTypeTarget = affected,
      adjustParameter = adjust,
      applyFunction = applyFunction,
      applyChange = change
    )
