package model.events.movementEvent

import model.world.{LocalPercentageMovement, MovementStrategy, Static, GlobalLogicMovement}

object MovementStrategyDispatcher:
  def logicFor(strategy: MovementStrategy): MovementLogic = strategy match
    case LocalPercentageMovement => LocalPercentageLogic
    case GlobalLogicMovement     => GlobalLogic
    case Static                  => StaticLogic

