package model.plague.db

import model.plague.Trait

trait TraitContainer:
  def allBasics: List[Trait]
