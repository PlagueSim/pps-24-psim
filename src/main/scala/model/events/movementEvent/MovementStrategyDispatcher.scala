package model.events.movementEvent

import model.world.{GlobalRandomMovement, LocalPercentageMovement, MovementStrategy, Static}

object MovementStrategyDispatcher:
  def logicFor(strategy: MovementStrategy): MovementLogic = strategy match
    case LocalPercentageMovement => LocalPercentageLogic
    case GlobalRandomMovement    => GlobalRandomLogic
    case Static                  => StaticLogic

