package view.world

import model.world.World
import view.event.ViewEvent


trait View {
  def render(world: World): Unit
  def handleEvent(event: ViewEvent): Unit
  def setEventHandler(handler: ViewEvent => Unit): Unit
}