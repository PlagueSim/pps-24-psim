package model.infection

import Probability.Probability
import model.world.Node
import model.plague.Disease

/**
 * Factory object for creating instances of [[PopulationEffect]].
 * This object provides a flexible way to construct population effects by composing functions.
 */
private[infection] object PopulationEffectComposer:
  /**
   * A concrete implementation of [[PopulationEffect]] that applies a series of functions
   * to a node's population based on a disease's characteristics.
   */
  private case class FunctionalPopulationEffect[A](
      canApply: Node => Boolean,
      extractParameter: Disease => Double,
      populationSelector: Node => A,
      adjustParameter: Double => Probability,
      calculateChange: (A, Probability) => Int,
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
  def apply[A](
      canApply: Node => Boolean,
      parameterExtractor: Disease => Double,
      populationSelector: Node => A,
      parameterAdjuster: Double => Probability = Probability.fromPercentage,
      changeCalculator: (A, Probability) => Int,
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
