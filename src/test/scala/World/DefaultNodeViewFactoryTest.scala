package view

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.World.*

class DefaultNodeViewFactoryTest extends AnyFlatSpec with Matchers:

  private val dummyOnMoved = () => ()

  private val factory = new DefaultNodeViewFactory(dummyOnMoved)

  "DefaultNodeViewFactory" should "create a NodeView with 4 visuals" in:
    val node = Node.withPopulation(100).withInfected(10).build()
    val view = factory.createNode("testNode", node, (50.0, 60.0))
    view.visuals should have size 4

  it should "create a Circle at the specified position" in:
    val node = Node.withPopulation(100).withInfected(10).build()
    val view = factory.createNode("testNode", node, (10.0, 20.0))

    // Extract the Circle
    val circle = view.visuals.collectFirst {
      case c: javafx.scene.shape.Circle => c
    }.getOrElse(fail("Circle not found in visuals"))

    circle.getCenterX shouldBe 10.0
    circle.getCenterY shouldBe 20.0
    circle.getRadius shouldBe 15.0

  it should "create labels with correct text" in:
    val node = Node.withPopulation(42).withInfected(7).build()
    val view = factory.createNode("Node42", node, (0.0, 0.0))

    val labels = view.visuals.collect {
      case t: javafx.scene.text.Text => t
    }

    labels should have size 3

    labels.map(_.getText) should contain allOf (
      "Node: Node42",
      "Pop: 42",
      "Infected: 7"
    )

  it should "return the correct position from position() before dragging" in:
    val node = Node.withPopulation(1).withInfected(1).build()
    val view = factory.createNode("node", node, (123.0, 456.0))
    val (x, y) = view.position()
    x shouldBe 123.0
    y shouldBe 456.0
