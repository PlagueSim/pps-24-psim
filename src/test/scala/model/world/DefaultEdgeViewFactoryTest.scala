package model.world

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalafx.scene.paint.Color
import model.world.*
import view.world.DefaultEdgeViewFactory

class DefaultEdgeViewFactoryTest extends AnyFlatSpec with Matchers:

  private val edgeStyles = Map(
    EdgeType.Land -> ((-1, -2), Color.Green),
    EdgeType.Sea  -> ((0, 0), Color.Blue),
    EdgeType.Air  -> ((5, 5), Color.Red)
  )

  private val factory = new DefaultEdgeViewFactory(edgeStyles)

  "DefaultEdgeViewFactory" should "create a Line with correct coordinates and color for Land edge" in:
    val edge = Edge("A", "B", EdgeType.Land)
    val positions = Map(
      "A" -> (10.0, 20.0),
      "B" -> (30.0, 40.0)
    )

    val line = factory.createEdge(edge, positions).asInstanceOf[javafx.scene.shape.Line]

    line.getStartX shouldBe 9.0
    line.getStartY shouldBe 18.0
    line.getEndX shouldBe 29.0
    line.getEndY shouldBe 38.0
    line.getStroke shouldBe Color.Green.delegate

  it should "create a Line with correct coordinates and color for Sea edge" in:
    val edge = Edge("A", "B", EdgeType.Sea)
    val positions = Map(
      "A" -> (0.0, 0.0),
      "B" -> (100.0, 200.0)
    )

    val line = factory.createEdge(edge, positions).asInstanceOf[javafx.scene.shape.Line]

    line.getStartX shouldBe 0.0
    line.getStartY shouldBe 0.0
    line.getEndX shouldBe 100.0
    line.getEndY shouldBe 200.0
    line.getStroke shouldBe Color.Blue.delegate

  it should "create a Line with correct coordinates and color for Air edge" in:
    val edge = Edge("A", "B", EdgeType.Air)
    val positions = Map(
      "A" -> (1.0, 1.0),
      "B" -> (4.0, 9.0)
    )

    val line = factory.createEdge(edge, positions).asInstanceOf[javafx.scene.shape.Line]

    line.getStartX shouldBe 6.0
    line.getStartY shouldBe 6.0
    line.getEndX shouldBe 9.0
    line.getEndY shouldBe 14.0
    line.getStroke shouldBe Color.Red.delegate
