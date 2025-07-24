package view.world

import model.world.World
import controller.WorldController

object WorldViewFactory:

  def create(world: World): WorldView =
    val controller = new WorldController(world)
    new WorldView(controller)
