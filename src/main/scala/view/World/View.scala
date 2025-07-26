package view.world

import model.world.World
import view.event.ViewEvent
import view.updatables.UpdatableView


trait View extends UpdatableView {
  def render(world: World): Unit
  def handleEvent(event: ViewEvent): Unit
  def setEventHandler(handler: ViewEvent => Unit): Unit
}