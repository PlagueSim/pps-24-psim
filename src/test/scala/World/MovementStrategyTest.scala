package World

import model.World.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementStrategyTest extends AnyFlatSpec with Matchers {


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