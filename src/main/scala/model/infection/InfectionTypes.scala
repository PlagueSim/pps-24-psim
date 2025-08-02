package model.infection

import jdk.dynalink.NamedOperation
import model.infection.Probability.Probability
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import model.world.Node
import org.apache.commons.math3.distribution.{BinomialDistribution, HypergeometricDistribution}
import org.apache.commons.math3.util.FastMath.pow

/** Object containing all the infection types */
object InfectionTypes:

  private val STANDARD_CAN_APPLY: Node => Boolean =
    n => n.infected > 0 && n.population - n.infected > 0

  val StandardInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Standard infection logic with temperature */
  def WithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  val ProbabilisticInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) =>
        val binomial = new BinomialDistribution(healthy, prob.value)
        binomial.sample()
      ,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Probabilistic infection logic with temperature */
  def ProbabilisticInfectionWithTemperature(temp: Double)(using
      adjuster: TemperatureAdjuster
  ): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      parameterAdjuster =
        p => Probability.fromPercentage(adjuster.adjustForTemperature(p, temp)),
      changeCalculator = (healthy, prob) =>
        val binomial = new BinomialDistribution(healthy, prob.value)
        binomial.sample()
      ,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  /** Advanced probabilistic infection logic that allows for a variable number
    * of affected individuals
    */
  def AdvancedProbabilistic(affectable: Int): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => {
        val totalAffected =
          try {
            Math.multiplyExact(node.infected, affectable)
          } catch {
            case e: ArithmeticException =>
              println(
                s"Overflow occurred while calculating totalAffected: ${e.getMessage}"
              )
              Int.MaxValue // Fallback to a safe value
          }
        val hyp = new HypergeometricDistribution(
          node.population,
          node.population - node.infected,
          totalAffected
        )
        hyp.sample()
      },
      changeCalculator = (healthy, prob) =>
        new BinomialDistribution(healthy, prob.value).sample(),
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  def SimplifiedProbabilistic(affectable: Int): PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => {
        val healthy = node.population - node.infected
        val p = 1.0 - pow((node.population - 1 - affectable).toDouble / (node.population - 1).toDouble, node.infected.toDouble)
        BinomialDistribution(healthy, p * 0.1).sample()
      },
      changeCalculator = (healthy, prob) =>
        new BinomialDistribution(healthy, prob.value).sample(),
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  // --- Inizio Nuova Logica "Contatti Limitati" ---

  private def estimatedLimitedContactsChangeCalculator(healthy: Int, prob: Probability): Int = {
    val cappedContacts = (healthy * 0.2).toInt.max(1)
    val expectedNewInfected = cappedContacts * prob.value
    val noise = scala.util.Random.between(0.95, 1.1)
    val proposed = (expectedNewInfected * noise).round.toInt.min(healthy)
    if (healthy <= 5 && proposed < healthy) healthy else proposed.max(0)
  }

  val LimitedContactsInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = n => n.infected > 0 && n.population - n.infected > 0,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = estimatedLimitedContactsChangeCalculator,
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  // Add to InfectionTypes.scala

  val LimitedContactInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => node.population - node.infected,
      changeCalculator = (healthy, prob) => {
        // Base contacts per infected person (scales with population density)
        val baseContacts = 10 + (healthy / 1000000) // 10-110 contacts based on population

        // Calculate effective contacts considering infection spread
        val effectiveContacts = baseContacts * (1.0 - math.exp(-healthy.toDouble / 100000000))

        // New infections = (infected count * contacts) * infection probability
        // But we approximate infected count from healthy population
        val infectedEstimate = healthy * 0.1 / (prob.value + 0.01) // Inverse relationship

        val baseInfections = (infectedEstimate * effectiveContacts * prob.value).toInt

        // Dynamic scaling - slow start, fast middle, slow finish
        val phaseFactor = healthy match {
          case h if h > 10000000 => 0.8 // Large populations: conservative growth
          case h if h > 1000000 => 1.2 // Medium populations: accelerated growth
          case h if h > 1000 => 1.5 // Small populations: faster growth
          case _ => 2.0 // Very small: aggressive growth
        }

        // Apply scaling and randomness
        val scaled = (baseInfections * phaseFactor).toInt
        val result = scaled.min(healthy).max(0)

        // Ensure progression but prevent stalls
        if (result == 0 && healthy > 0 && prob.value > 0.001) 1 else result
      },
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )

  val LogisticGrowthInfection: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.infectivity,
      populationSelector = node => {
        // Return a custom value encoding both population and infected count
        // Using bit shifting to pack two integers into one Long
        val total = node.population
        val infected = node.infected
        ((total.toLong << 32) | infected.toLong).toInt
      },
      changeCalculator = (encoded, prob) => {
        // Unpack the encoded value
        val total = (encoded.toLong >>> 32).toInt
        val infected = (encoded & 0xFFFFFFFFL).toInt
        val healthy = total - infected

        if (healthy <= 0 || infected <= 0) 0
        else {
          // Calculate infection probability with logistic growth model
          val baseProb = prob.value
          val prevalence = infected.toDouble / total.toDouble
          val growthFactor = 1 - prevalence // Higher when prevalence is low
          val densityFactor = 1 - math.exp(-total.toDouble / 1000000) // Adjust for population density

          // Calculate effective contacts with dynamic scaling
          val baseContacts = 10 + (10 * densityFactor).toInt
          val effectiveContacts = (baseContacts * growthFactor * densityFactor).max(1)

          // New infections = infected * contacts * probability
          val rawInfections = infected * effectiveContacts * baseProb

          // Apply logistic curve to growth
          val logisticGrowth = rawInfections * (1 - prevalence)

          // Apply noise for natural variation
          val noise = 0.8 + scala.util.Random.nextDouble() * 0.4
          val infectedCount = (logisticGrowth * noise).toInt

          // Ensure progression but prevent over-infection
          val result = infectedCount.min(healthy).max(0)

          // Always infect at least 1 when possible
          if (result == 0 && healthy > 0 && baseProb > 0) 1 else result
        }
      },
      changeApplier = (node, infected) => node.increaseInfection(infected)
    )



