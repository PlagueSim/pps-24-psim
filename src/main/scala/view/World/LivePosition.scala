package view.world
import model.world.Types.*
/* class representing a live position of a node in the world. */
case class LivePosition(get: () => (PosX, PosY))