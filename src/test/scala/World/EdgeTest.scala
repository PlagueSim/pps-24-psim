package World

import model.World.Edge
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgeTest extends AnyFlatSpec with Matchers:

  "Edge.apply" should "normalize node order to ensure consistency" in {
    val e1 = Edge("A", "B")
    val e2 = Edge("B", "A")
    e1 shouldEqual e2
    e1.nodeA should be <= e1.nodeB
  }

  it should "retain weight during normalization" in {
    val e1 = Edge("X", "Z", weight = 2.5)
    val e2 = Edge("Z", "X", weight = 2.5)
    e1 shouldEqual e2
    e1.weight shouldEqual 2.5
  }


  "connects" should "return true if the node is part of the edge" in {
    val edge = Edge("A", "B")
    edge.connects("A") shouldBe true
    edge.connects("B") shouldBe true
  }

  it should "return false if the node is not part of the edge" in {
    val edge = Edge("A", "B")
    edge.connects("C") shouldBe false
  }



