package view.world

import model.world.World
import view.updatables.UpdatableView

/**
 * View is a trait that defines the contract for rendering the world.
 * It extends UpdatableView, which means it can be updated with new data.
 * The render method takes a World object and is responsible for displaying
 * the current state of the world.
 * */
trait UpdatableWorldView extends UpdatableView {
  def render(world: World): Unit
}