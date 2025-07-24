package model.world

/* Represents a node in the simulation. */
case class Node private (
                          population: Int,
                          infected: Int,
                          died: Int,
                        ):
  /* update the number of people died in a node */
  def updateDied(died: Int): Node =
    require(died >= 0, "Died must be >= 0")
    require(died <= this.infected, "Died cannot exceed infected")
    this.copy(
      population = this.population - died,
      infected = this.infected - died,
      died = this.died + died
    )



object Node:


  /* Starts the building process for a Node by specifying the mandatory population field. */
  def withPopulation(value: Int): Builder =
    require(value >= 0, "Population must be >= 0")
    Node.Builder(population = value)


  /* Builder class for incrementally constructing a Node with validation. */
  final case class Builder (
                                     population: Int,
                                     infected: Int = 0,
                                     died: Int = 0,
                                   ):


    /* Sets the number of infected individuals. */
    def withInfected(value: Int): Builder =
      require(value >= 0, "Infected must be >= 0")
      copy(infected = value)

    /* Sets the number of died individuals. */
    def withDied(value: Int): Builder =
      require(value >= 0, "Died must be >= 0")
      copy(died = value)
    

    /*
     * Finalizes and returns the constructed Node.
     * Validates that infected people do not exceed population.
     */
    def build(): Node =
      require(infected <= population, "Infected cannot exceed population")
      Node(population, infected, died)

  /* Extension methods for immutable Node operations */
  extension (node: Node)

    /* Returns the percentage of the population that is infected.*/
    def infectedPercentage(): Double =
      if node.population == 0 then 0.0
      else node.infected.toDouble / node.population
    

    /* Increases the infected count by the given value, capped at total population. */
    def applyInfection(count: Int): Node =
      node.copy(infected = (node.infected + count).min(node.population))

    /* Reduces the infected count by the given value, but never below zero. */
    def heal(count: Int): Node =
      node.copy(infected = (node.infected - count).max(0))

    /* Increases the population count by the given value. */
    def increasePopulation(count: Int): Node =
      node.copy(population = node.population + count)

    /* Decreases the population count by the given value, but never below zero. */
    def decreasePopulation(count: Int): Node =
      node.copy(population = (node.population - count).max(0))
