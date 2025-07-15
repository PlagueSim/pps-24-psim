package events

import model.core.SimulationState
import model.cure.Cure
import model.events.InfectionEvent
import model.infection.InfectionStrategy
import model.infection.InfectionStrategy.TemperatureAwareInfection
import model.plague.Disease
import model.plague.Symptoms.*
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InfectionEventTest extends AnyFlatSpec with Matchers:
  "InfectionEvent" should "apply infection logic to all nodes in the world" in:

    val STARTING_DAY: Int = 0
    val STARTING_YEAR: Int = 2025

    val node = Node.withPopulation(100).withInfected(1).build()

    val world = World(
      nodes = Map("A" -> node, "B" -> node),
      Set(Edge("A", "B", EdgeType.Land)),
      Map(Static -> 1)
    )

    SimulationState(
      BasicYear(Day(STARTING_DAY), Year(STARTING_YEAR)),
      Disease("StandardDisease", Set(pulmonaryEdema), 1),
      Cure(),
      world,
      InfectionStrategy.StandardInfection
    )
    val infectionEvent = InfectionEvent()

    val initialState = SimulationState.createStandardSimulationState()

    val modifiedState = infectionEvent.modifyFunction(initialState)

    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should be (5)
    )

  it should "calculate infection correctly based on the infection logic" in:

    val degree: Double = 0.0

    val state = SimulationState.createStandardSimulationState().replace(TemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should be (4)
    )

  it should "behave correctly with 10 infectivity" in:
    val state = SimulationState.createStandardSimulationState()
      .replace(Disease("test", Set(necrosis), 1))

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should be (10)
    )

  "With a temperature really low the infection" should "not spread" in:
    val degree: Double = -100.0

    val state = SimulationState.createStandardSimulationState().replace(TemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should be (1)
    )

  it should "also not spread with a temperature really high" in:
    val degree: Double = 100.0

    val state = SimulationState.createStandardSimulationState().replace(TemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should be (1)
    )

  "Calling multiple times the infection event" should "increment the infected count correctly" in:
    val state = SimulationState.createStandardSimulationState()

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state) //5
    val news = state.replace(World(modifiedState, state.world.edges, state.world.movements))
    val y = infectionEvent.modifyFunction(news) //28

    y.map((s, n) => n).foreach(x =>
      x.infected should be(28)
    )

    val news2 = news.replace(World(y, state.world.edges, state.world.movements))

    val z = infectionEvent.modifyFunction(news2)

    z.map((s, n) => n).foreach(x =>
      x.infected should be(100)
    )

  "The probabilistic infection" should "behave correctly" in:
    val state = SimulationState.createStandardSimulationState()
      .replace(InfectionStrategy.ProbabilisticInfection)

    val infectionEvent = InfectionEvent()
    val modifiedState = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x =>
      x.infected should (be >= 1 and be <= 15)
    )

    val news = state.replace(World(modifiedState, state.world.edges, state.world.movements))
    val y = infectionEvent.modifyFunction(news)
    y.map((s, n) => n).foreach(x =>
      x.infected should (be >= 5 and be <= 50)
    )