package model.World

/**
 * Represents a node in the simulation.
 * @param population total number of people in the node
 * @param infected number of infected people in the node
 * @param cureEffectiveness percentage [0.0 - 1.0] of effectiveness when curing
 */
case class Node private (
                          population: Int,
                          infected: Int,
                          cureEffectiveness: Double
                        )

object Node:

  /**
   * Starts the building process for a Node by specifying the mandatory population field.
   *
   * @param value total number of people in the node
   * @return a Builder instance for fluent node configuration
   */
  def withPopulation(value: Int): Builder =
    require(value >= 0, "Population must be >= 0")
    Node.Builder(population = value)


  /**
   * Builder class for incrementally constructing a Node with validation.
   *
   * @param population total number of people (mandatory)
   * @param infected optional number of infected people (default = 0)
   * @param cureEffectiveness optional cure percentage (default = 0.0)
   */
  final case class Builder (
                                     population: Int,
                                     infected: Int = 0,
                                     cureEffectiveness: Double = 0.0
                                   ):


    /**
     * Sets the number of infected individuals.
     *
     * @param value number of infected people (must be >= 0)
     * @return a copy of the Builder with updated infected value
     */
    def withInfected(value: Int): Builder =
      require(value >= 0, "Infected must be >= 0")
      copy(infected = value)

    /**
     * Sets the effectiveness of the cure.
     *
     * @param value cure effectiveness as a percentage between 0.0 and 1.0
     * @return a copy of the Builder with updated cure effectiveness
     */
    def withCureEffectiveness(value: Double): Builder =
      require(value >= 0.0 && value <= 1.0, "Cure effectiveness must be between 0.0 and 1.0")
      copy(cureEffectiveness = value)

    /**
     * Finalizes and returns the constructed Node.
     * Validates that infected people do not exceed population.
     *
     * @return a fully constructed Node
     */
    def build(): Node =
      require(infected <= population, "Infected cannot exceed population")
      Node(population, infected, cureEffectiveness)

  /** Extension methods for immutable Node operations */
  extension (node: Node)

    /**
     * Returns the percentage of the population that is infected.
     *
     * @return infected individuals as a fraction of the population (0.0 if population is 0)
     */
    def infectedPercentage(): Double =
      if node.population == 0 then 0.0
      else node.infected.toDouble / node.population

    /**
     * Applies a cure to the node based on the current cureEffectiveness.
     * The infected count is reduced accordingly.
     *
     * @return a new Node with updated infected count
     */
    def applyCure(): Node =
      val healed = (node.infected * node.cureEffectiveness).toInt
      node.copy(infected = (node.infected - healed).max(0))

    /**
     * Increases the infected count by the given value, capped at total population.
     *
     * @param count number of people to infect
     * @return a new Node with updated infected count
     */
    def applyInfection(count: Int): Node =
      node.copy(infected = (node.infected + count).min(node.population))

    /**
     * Reduces the infected count by the given value, but never below zero.
     *
     * @param count number of people to heal
     * @return a new Node with updated infected count
     */
    def heal(count: Int): Node =
      node.copy(infected = (node.infected - count).max(0))

    /**
     * Increases the population count by the given value.
     *
     * @param count number of people to add to the population
     * @return a new Node with updated population
     */
    def increasePopulation(count: Int): Node =
      node.copy(population = node.population + count)

    /**
     * Decreases the population count by the given value, but never below zero.
     *
     * @param count number of people to remove from the population
     * @return a new Node with updated population
     */
    def decreasePopulation(count: Int): Node =
      node.copy(population = (node.population - count).max(0))
