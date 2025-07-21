package view.world

import scalafx.scene.paint.Color
import model.world.*
import controller.WorldController

object WorldViewFactory:

  def create(world: World): WorldView =
    val layout = new CircularLayout(
      radius = 200,
      centerX = 400,
      centerY = 250
    )

    val controller = new WorldController(world)
    
    val edgeFactory = new DefaultEdgeViewFactory(
      edgeStyle = Map(
        EdgeType.Land -> ((-8, -8), Color.Green),
        EdgeType.Sea  -> ((0, 0), Color.Blue),
        EdgeType.Air  -> ((8, 8), Color.Red)
      )
    )
    lazy val view: WorldView = new WorldView(
      controller,
      layout,
      nodeFactory,
      edgeFactory
    )

    lazy val nodeFactory = new DefaultNodeViewFactory(() => view.redrawEdges())

    view
