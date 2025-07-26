package model.events.movementEvent

object MovementValidator:
  def validateDestinations(destinations: Set[String]): Unit =
    require(destinations.isEmpty, s"Movement towards unknown nodes detected: ${destinations.mkString(", ")}")

