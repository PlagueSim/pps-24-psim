package view

import scalafx.scene.paint.Color
import model.World.*

object WorldViewFactory:

  def create(world: World): WorldView2 =
    val layout = new CircularLayout(
      radius = 200,
      centerX = 400,
      centerY = 250
    )

    // Create placeholder
    var view: WorldView2 = null

    val nodeFactory = new DefaultNodeViewFactory(() => view.redrawEdges())

    val edgeFactory = new DefaultEdgeViewFactory(
      edgeStyle = Map(
        EdgeType.Land -> ((-8, -8), Color.Green),
        EdgeType.Sea  -> ((0, 0), Color.Blue),
        EdgeType.Air  -> ((8, 8), Color.Red)
      )
    )

    view = new WorldView2(
      world = world,
      layout = layout,
      nodeFactory = nodeFactory,
      edgeFactory = edgeFactory
    )

    view
