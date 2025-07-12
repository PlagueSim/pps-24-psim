package model.world

import model.World.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementStrategyTest extends AnyFlatSpec with Matchers {

  "MovementStrategy.random" should "create a RandomMove with valid probability" in {
    val strategy = MovementStrategy.random(0.3)
    strategy shouldBe a [RandomMove]
    strategy.probability shouldBe 0.3
  }

  it should "throw an exception for invalid probability" in {
    an [IllegalArgumentException] should be thrownBy MovementStrategy.random(-0.1)
    an [IllegalArgumentException] should be thrownBy MovementStrategy.random(1.1)
  }

  "MovementStrategy.targeted" should "create a TargetedMove with valid intensity" in {
    val strategy = MovementStrategy.targeted("nodeA", 0.8)
    strategy shouldBe a [TargetedMove]
    strategy.targetNode shouldBe "nodeA"
    strategy.intensity shouldBe 0.8
  }

  it should "throw an exception for invalid intensity" in {
    an [IllegalArgumentException] should be thrownBy MovementStrategy.targeted("nodeB", -0.5)
    an [IllegalArgumentException] should be thrownBy MovementStrategy.targeted("nodeB", 1.5)
  }

  "MovementStrategy.isMobile" should "correctly identify Static as not mobile" in {
    MovementStrategy.isMobile(Static) shouldBe false
  }

  it should "correctly identify RandomMove and TargetedMove as mobile" in {
    MovementStrategy.isMobile(RandomMove(0.2)) shouldBe true
    MovementStrategy.isMobile(TargetedMove("X", 1.0)) shouldBe true
  }

  "Default strategies" should "be correctly defined" in {
    MovementStrategy.DefaultRandom shouldBe RandomMove(0.5)
    MovementStrategy.DefaultTarget shouldBe TargetedMove("capital", 1.0)
  }
}