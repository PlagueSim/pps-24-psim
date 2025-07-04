package World

import model.World.{Edge, EdgeType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgeTest extends AnyFlatSpec with Matchers:

  "Edge.apply" should "normalize node order to ensure consistency" in {
    val e1 = Edge("A", "B", EdgeType.Land)
    val e2 = Edge("B", "A", EdgeType.Land)
    e1 shouldEqual e2
    e1.nodeA should be <= e1.nodeB
  }

  it should "retain weight during normalization" in {
    val e1 = Edge("X", "Z", EdgeType.Sea, weight = 2.5)
    val e2 = Edge("Z", "X", EdgeType.Sea, weight = 2.5)
    e1 shouldEqual e2
    e1.weight shouldEqual 2.5
  }

  it should "differentiate edges by typology" in {
    val landEdge = Edge("A", "B", EdgeType.Land)
    val seaEdge  = Edge("A", "B", EdgeType.Sea)
    landEdge should not equal seaEdge
  }

  "connects" should "return true if the node is part of the edge" in {
    val edge = Edge("A", "B", EdgeType.Air)
    edge.connects("A") shouldBe true
    edge.connects("B") shouldBe true
  }

  it should "return false if the node is not part of the edge" in {
    val edge = Edge("A", "B", EdgeType.Air)
    edge.connects("C") shouldBe false
  }

  "other" should "return the opposite node if input is nodeA or nodeB" in {
    val edge = Edge("A", "B", EdgeType.Sea)
    edge.other("A") shouldEqual Some("B")
    edge.other("B") shouldEqual Some("A")
  }

  it should "return None if the node is not part of the edge" in {
    val edge = Edge("A", "B", EdgeType.Sea)
    edge.other("C") shouldBe None
  }
