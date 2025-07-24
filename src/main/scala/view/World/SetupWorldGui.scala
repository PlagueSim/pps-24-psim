// src/module/WorldModule.scala
package view.world

import model.world.World
import model.world.WorldFactory
import view.world.WorldView
import view.world.View
import controller.WorldController

case class SetupWorldGui(worldView: WorldView, initialWorld: World, controller: WorldController)

object SetupWorldGui:
  def initialize(): SetupWorldGui =
    val view = new WorldView()
    val world = WorldFactory.mockWorld()
    val controller = WorldController(world, view)

    view.setEventHandler(controller.handle)
    view.render(world)
    SetupWorldGui(view, world, controller)
