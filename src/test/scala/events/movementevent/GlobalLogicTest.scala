package events.movementevent

import model.events.movementEvent.GlobalLogic
import model.world.*
import model.world.MovementComputation.PeopleMovement
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Random

class GlobalLogicTest extends AnyFlatSpec with Matchers:

  "GlobalLogic" should "not generate movement when all nodes have zero population" in:
    val world: World = World(
      nodes = Map(
        "A" -> Node.withPopulation(0).withInfected(0).withDied(0).build(),
        "B" -> Node.withPopulation(0).withInfected(0).withDied(0).build()
      ),
      edges = Map("A-B-Land" -> Edge("A", "B", EdgeType.Land)),
      movements = Map(GlobalLogicMovement -> 1.0)
    )
  
    val result: Iterable[PeopleMovement] = GlobalLogic.compute(world, 1.0, new Random())
    result shouldBe empty

  it should "not generate movement when the edge is closed" in:
    val world: World = World(
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(20).withDied(0).build(),
        "B" -> Node.withPopulation(100).withInfected(10).withDied(0).build()
      ),
      edges = Map("A-B-Land" -> Edge("A", "B", EdgeType.Land).close),
      movements = Map(GlobalLogicMovement -> 1.0)
    )

    GlobalLogic.compute(world, 1.0, new Random()) shouldBe empty

  it should "generate movement when the edge is open, population > 0, and random < edge probability" in:
    val world: World = World(
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(20).withDied(0).build(),
        "B" -> Node.withPopulation(100).withInfected(10).withDied(0).build()
      ),
      edges = Map("A-B-Air" -> Edge("A", "B", EdgeType.Air)),
      movements = Map(GlobalLogicMovement -> 1.0)
    )
  
    val fixedRandom: Random = new Random:
      override def nextDouble(): Double = 0.1
  
    val result: Seq[PeopleMovement] = GlobalLogic.compute(world, 1.0, fixedRandom).toList
  
    result should not be empty
    val mv: PeopleMovement = result.head
    mv.from shouldBe "A"
    mv.to shouldBe "B"
    mv.amount should be <= GlobalLogic.edgeMovementConfig.capacity(EdgeType.Land)

  it should "not generate movement when the edge is open but random >= edge probability" in:
    val world: World = World(
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(20).withDied(0).build(),
        "B" -> Node.withPopulation(100).withInfected(10).withDied(0).build()
      ),
      edges = Map("A-B-Air" -> Edge("A", "B", EdgeType.Air)),
      movements = Map(GlobalLogicMovement -> 1.0)
    )
  
    val fixedRandom: Random = new Random:
      override def nextDouble(): Double = 0.9  // edge probability is 0.15
  
    val result: Iterable[PeopleMovement] = GlobalLogic.compute(world, 1.0, fixedRandom)
    result shouldBe empty
