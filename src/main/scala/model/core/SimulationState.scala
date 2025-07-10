package model.core

import model.cure.Cure
import model.plague.Disease
import model.time.Time

//fix replace not type-safe
case class SimulationState(time: Time, disease: Disease, cure: Cure):
  def replace[A](newValue: A): SimulationState = newValue match
    case newTime: Time       => this.copy(time = newTime)
    case newDisease: Disease => this.copy(disease = newDisease)
    case newCure: Cure       => this.copy(cure = newCure)
    case _                   => this
