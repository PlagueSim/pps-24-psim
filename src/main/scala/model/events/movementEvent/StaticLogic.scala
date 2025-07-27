package model.events.movementEvent

import model.world.{Node, World}

object StaticLogic extends MovementLogic:
  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
                      ): List[(String, String, Int)] = Nil

