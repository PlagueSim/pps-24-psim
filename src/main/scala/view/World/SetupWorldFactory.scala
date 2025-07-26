// src/module/WorldModule.scala
package view.world

import model.world.World
import model.world.WorldFactory
import controller.WorldController
import view.event.StartWorldSimulation

case class SetupWorldFactory(worldView: VisualView, initialWorld: World, controller: WorldController)

object SetupWorldFactory:
  def initializeWorldGui(worldInput: World): SetupWorldFactory =
    val view: VisualView = new WorldView()
    val world = worldInput
    val controller = WorldController(world, view)

    view.setEventHandler(controller.handle)
    view.handleEvent(StartWorldSimulation)
    SetupWorldFactory(view, world, controller)

  def initializeWorldConsole(worldInput: World): SetupWorldFactory =
    val view: VisualView = new ConsoleView()
    val world = worldInput
    val controller = WorldController(world, view)

    view.setEventHandler(controller.handle)
    view.handleEvent(StartWorldSimulation)
    SetupWorldFactory(view, world, controller)


