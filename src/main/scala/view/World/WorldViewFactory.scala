package view

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

    var viewRef: WorldView = null

    val nodeFactory = new DefaultNodeViewFactory(() => viewRef.redrawEdges())

    val edgeFactory = new DefaultEdgeViewFactory(
      edgeStyle = Map(
        EdgeType.Land -> ((-8, -8), Color.Green),
        EdgeType.Sea  -> ((0, 0), Color.Blue),
        EdgeType.Air  -> ((8, 8), Color.Red)
      )
    )

    val view = new WorldView(
      world = controller,
      layout = layout,
      nodeFactory = nodeFactory,
      edgeFactory = edgeFactory
    )

    viewRef = view

    view
