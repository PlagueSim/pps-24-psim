// src/module/WorldModule.scala
package view.world

import model.world.World
import model.world.WorldFactory
import controller.WorldController

case class SetupWorldFactory(worldView: VisualView, initialWorld: World, controller: WorldController)

object SetupWorldFactory:
  def initializeWorldGui(): SetupWorldFactory =
    val view: VisualView = new WorldView()
    val world = WorldFactory.mockWorld()
    val controller = WorldController(world, view)

    view.setEventHandler(controller.handle)
    view.render(world)
    SetupWorldFactory(view, world, controller)

  def initializeWorldConsole(): SetupWorldFactory =
    val view: VisualView = new ConsoleView()
    val world = WorldFactory.mockWorld()
    val controller = WorldController(world, view)

    view.setEventHandler(controller.handle)
    view.render(world)
    SetupWorldFactory(view, world, controller)


