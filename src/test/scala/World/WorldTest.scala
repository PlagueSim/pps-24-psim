package World

import model.World.World
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WorldTest extends AnyFlatSpec with Matchers:

  "World.empty" should "start with no nodes, edges, or movement strategies" in {
    val world = World.empty
    world.nodes shouldBe empty
    world.edges shouldBe empty
    world.movements shouldBe empty
  }



