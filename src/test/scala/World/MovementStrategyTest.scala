package World

import model.World.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementStrategyTest extends AnyFlatSpec with Matchers {



  "Default strategies" should "be correctly defined" in {
    MovementStrategy.DefaultRandom shouldBe RandomMove(0.5)
    MovementStrategy.DefaultTarget shouldBe TargetedMove("capital", 1.0)
  }
} 