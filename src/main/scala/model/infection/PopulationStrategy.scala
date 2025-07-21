package model.infection

import model.plague.Disease
import model.world.Node

trait PopulationStrategy:
  def applyToPopulation(node: Node, disease: Disease): Node