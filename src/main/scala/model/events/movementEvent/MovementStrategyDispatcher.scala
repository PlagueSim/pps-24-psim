package model.events.movementEvent

import model.world.{LocalPercentageMovement, MovementStrategy, Static, GlobalLogicMovement}

object MovementStrategyDispatcher:
  /*
  * MovementStrategyDispatcher is responsible for dispatching the correct logic
  * based on the MovementStrategy provided.
  * It returns the appropriate MovementLogic instance
  * */
  def logicFor(strategy: MovementStrategy): MovementLogic = strategy match
    case LocalPercentageMovement => LocalPercentageLogic
    case GlobalLogicMovement     => GlobalLogic
    case Static                  => StaticLogic
    

