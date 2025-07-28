package model.infection

import Probability.Probability
import model.world.Node
import model.plague.Disease

/**
 * Factory object for creating instances of [[PopulationEffect]] c
 * This object provides a flexible way to construct population effects by composing functions.
 */
private[infection] object PopulationEffectBuilder:
  /**
   * A concrete implementation of [[PopulationEffect]] that applies a series of functions
   * to a node's population based on a disease's characteristics.
   */
  private case class FunctionalPopulationEffect(
      canApply: Node => Boolean,
      extractParameter: Disease => Double,
      populationSelector: Node => Int,
      adjustParameter: Double => Probability,
      calculateChange: (Int, Probability) => Int,
      applyChange: (Node, Int) => Node
  ) extends PopulationEffect:
    /**
     * Applies the population effect to a given node if the conditions are met.
     */
    override def applyToPopulation(node: Node, disease: Disease): Node =
      if canApply(node) then
        lazy val rawParam       = extractParameter(disease)
        lazy val probability    = adjustParameter(rawParam)
        lazy val basePopulation = populationSelector(node)
        val change              = calculateChange(basePopulation, probability)
        applyChange(node, change)
      else node

  /**
   * Constructs a [[PopulationEffect]] by composing a set of functions.
   */
  def apply(
      canApply: Node => Boolean,
      parameterExtractor: Disease => Double,
      populationSelector: Node => Int,
      parameterAdjuster: Double => Probability = Probability.fromPercentage,
      changeCalculator: (Int, Probability) => Int,
      changeApplier: (Node, Int) => Node
  ): PopulationEffect =
    FunctionalPopulationEffect(
      canApply = canApply,
      extractParameter = parameterExtractor,
      populationSelector = populationSelector,
      adjustParameter = parameterAdjuster,
      calculateChange = changeCalculator,
      applyChange = changeApplier
    )
