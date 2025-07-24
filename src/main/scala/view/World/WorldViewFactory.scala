package view.world

import model.world.World
import controller.WorldController

object WorldViewFactory:

  /**
   * Creates a WorldView instance from a given World model.
   *
   * Initializes a WorldController with the provided world and uses it to build
   * the corresponding WorldView.
   *
   * @param world the world model to visualize
   * @return a new WorldView instance
   */
  def create(world: World): WorldView =
    val controller = new WorldController(world)
    new WorldView(controller)
