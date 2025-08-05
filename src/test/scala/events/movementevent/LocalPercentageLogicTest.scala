package events.movementevent

import model.events.movementEvent
import model.events.movementEvent.LocalPercentageLogic
import model.world.*
import model.world.MovementComputation.PeopleMovement
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.world.EdgeExtensions.getMapEdges

import scala.language.postfixOps
import scala.util.Random

class LocalPercentageLogicTest extends AnyFlatSpec with Matchers:

  "LocalPercentageLogic" should "not generate movement when node population is zero" in:
    val world = World(
      nodes = Map(
        "A" -> Node.withPopulation(0).withInfected(0).withDied(0).build(),
        "B" -> Node.withPopulation(0).withInfected(0).withDied(0).build()
      ),
      edges = List(Edge("A", "B", EdgeType.Land)).getMapEdges,
      movements = Map(LocalPercentageMovement -> 1.0)
    )

    val result = LocalPercentageLogic.compute(world, 1.0, new Random())
    result shouldBe empty

  it should "not generate movement when no edges are open" in:
    val world = World(
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(0).withDied(0).build(),
        "B" -> Node.withPopulation(100).withInfected(0).withDied(0).build()
      ),
      edges = List(Edge("A", "B", EdgeType.Land).close).getMapEdges,
      movements = Map(LocalPercentageMovement -> 1.0)
    )

    val result = LocalPercentageLogic.compute(world, 1.0, new Random())
    result shouldBe empty

  it should "generate movement to an open neighbor" in:
    val world = World(
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(0).withDied(0).build(),
        "B" -> Node.withPopulation(0).withInfected(0).withDied(0).build()
      ),
      edges = List(Edge("A", "B", EdgeType.Sea)).getMapEdges,
      movements = Map(LocalPercentageMovement -> 1.0)
    )

    val fixedRandom = new Random:
      override def nextInt(n: Int): Int = 0

    val result = LocalPercentageLogic.compute(world, 0.3, fixedRandom)
    result should have size 1

    val mv = result.head
    mv.from shouldBe "A"
    mv.to shouldBe "B"
    mv.amount shouldBe 30  // 100 * 0.3

  it should "limit movement to edge capacity if lower than base amount" in:
    val world = World(
      nodes = Map(
        "A" -> Node.withPopulation(200).withInfected(0).withDied(0).build(),
        "B" -> Node.withPopulation(200).withInfected(0).withDied(0).build()
      ),
      edges = List(Edge("A", "B", EdgeType.Air)).getMapEdges,
      movements = Map(LocalPercentageMovement -> 1.0)
    )

    val fixedRandom = new Random:
      override def nextInt(n: Int): Int = 0

    val result = LocalPercentageLogic.compute(world, 1.0, fixedRandom)
    result should have size 2

    val mv = result.head
    mv.amount <= LocalPercentageLogic.edgeMovementConfig.capacity(EdgeType.Air) shouldBe true
