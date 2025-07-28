package model.infection

import model.plague.Disease
import model.world.Node

/** Represents a population effect that can be applied to a node based on a disease */
trait PopulationEffect:
    /** Applies the population effect to a given node based on the disease*/
  def applyToPopulation(node: Node, disease: Disease): Node