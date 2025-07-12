package model.world

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import view.{CircularLayout}

class CircularLayoutTest extends AnyFlatSpec with Matchers:

  "CircularLayout" should "return empty map for empty input" in:
    val layout = new CircularLayout()
    layout.computePositions(Seq.empty) shouldBe Map.empty

  it should "place single node on the circle at angle 0" in:
    val layout = new CircularLayout(radius = 100, centerX = 0, centerY = 0)
    val positions = layout.computePositions(Seq("A"))
    positions("A") shouldBe (100.0, 0.0)

  it should "place 4 nodes at 0°, 90°, 180°, 270°" in:
    val layout = new CircularLayout(radius = 10, centerX = 0, centerY = 0)
    val positions = layout.computePositions(Seq("A", "B", "C", "D"))
    positions("A")._1 shouldBe 10.0 +- 0.0001
    positions("A")._2 shouldBe 0.0 +- 0.0001
    positions("B")._1 shouldBe 0.0 +- 0.0001
    positions("B")._2 shouldBe 10.0 +- 0.0001
    positions("C")._1 shouldBe -10.0 +- 0.0001
    positions("C")._2 shouldBe 0.0 +- 0.0001
    positions("D")._1 shouldBe 0.0 +- 0.0001
    positions("D")._2 shouldBe -10.0 +- 0.0001
