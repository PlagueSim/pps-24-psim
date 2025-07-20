package events

import model.core.SimulationState
import model.cure.Cure
import model.events.InfectionEvent
import model.infection.InfectionAndDeathPopulation.*
import model.infection.InfectionAndDeathPopulation.Infection.*
import model.infection.InfectionAndDeathPopulation.Infection.Death.{ProbabilisticDeath, StandardDeath}
import model.plague.Disease
import model.plague.Symptoms.*
import model.reaction.Reactions.StandardReactions
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InfectionEventTest extends AnyFlatSpec with Matchers:
  "InfectionEvent" should "apply infection logic to all nodes in the world" in:

    val STARTING_DAY: Int  = 0
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
      StandardInfection,
      StandardDeath,
      StandardReactions
    )
    val infectionEvent = InfectionEvent()

    val initialState = SimulationState.createStandardSimulationState()

    val modifiedState = infectionEvent.modifyFunction(initialState)

    modifiedState.map((s, n) => n).foreach(x => x.infected should be(5))

  it should "calculate infection correctly based on the infection logic" in:

    val degree: Double = 0.0

    val state = SimulationState
      .createStandardSimulationState()
      .replace(StandardTemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState  = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x => x.infected should be(4))

  it should "behave correctly with 20 infectivity" in:
    val state = SimulationState
      .createStandardSimulationState()
      .replace(Disease("test", Set(necrosis), 1))

    val infectionEvent = InfectionEvent()
    val modifiedState  = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x => x.infected should be(20))

  "With a temperature really low the infection" should "not spread" in:
    val degree: Double = -100.0

    val state = SimulationState
      .createStandardSimulationState()
      .replace(StandardTemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState  = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x => x.infected should be(1))

  it should "also not spread with a temperature really high" in:
    val degree: Double = 100.0

    val state = SimulationState
      .createStandardSimulationState()
      .replace(StandardTemperatureAwareInfection(degree))

    val infectionEvent = InfectionEvent()
    val modifiedState  = infectionEvent.modifyFunction(state)
    modifiedState.map((s, n) => n).foreach(x => x.infected should be(1))

  "Calling multiple times the infection event" should "increment the infected count correctly" in:
    val state = SimulationState.createStandardSimulationState()

    val infectionEvent = InfectionEvent()
    val modifiedState  = infectionEvent.modifyFunction(state) // 5
    val news           = state.replace(
      World(modifiedState, state.world.edges, state.world.movements)
    )
    val y = infectionEvent.modifyFunction(news) // 28

    y.map((s, n) => n).foreach(x => x.infected should be(28))

    val news2 = news.replace(World(y, state.world.edges, state.world.movements))

    val z = infectionEvent.modifyFunction(news2)

    z.map((s, n) => n).foreach(x => x.infected should be(100))

//  "The probabilistic infection" should "behave correctly" in:
//    val state = SimulationState
//      .createStandardSimulationState()
//      .replace(ProbabilisticInfection)
//
//    val infectionEvent = InfectionEvent()
//    val modifiedState  = infectionEvent.modifyFunction(state)
//    modifiedState
//      .map((s, n) => n)
//      .foreach(x => x.infected should (be >= 1 and be <= 15))
//
//    val news = state.replace(
//      World(modifiedState, state.world.edges, state.world.movements)
//    )
//    val y = infectionEvent.modifyFunction(news)
//    y.map((s, n) => n).foreach(x => x.infected should (be >= 5 and be <= 50))

  "The standard infection" should "infect 5 starting with 1 infected" in:
    val node = Node.withPopulation(100).withInfected(1).build()
    val ev   = StandardInfection.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    ev.infected should be(5)

  "The standardTemperatureAwareInfection" should "infect less than 5 because it is weakend by the temperature" in:
    val node = Node.withPopulation(100).withInfected(1).build()
    val ev2  = StandardTemperatureAwareInfection(0.0)
      .applyToPopulation(node, Disease("test", Set(pulmonaryEdema), 1))

    ev2.infected should be(4)

  it should "not infect if the temperature is too high or too low" in:
    val node = Node.withPopulation(100).withInfected(1).build()
    val ev3  = StandardTemperatureAwareInfection(100.0)
      .applyToPopulation(node, Disease("test", Set(pulmonaryEdema), 1))

    ev3.infected should be(1)

  "in a node with 0 population there" should "not be any death" in:
    val node = Node.withPopulation(0).build()
    val ev   = StandardDeath.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    ev.died should be(0)

  "in a node with 100 infected there" should "be 2 deaths" in:
    val node = Node.withPopulation(100).withInfected(100).build()
    val ev   = StandardDeath.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )
    ev.population should be(98)
    ev.died should be(2)

  it should "have 97 population after another death event" in:
    val node = Node.withPopulation(100).withInfected(100).build()
    val ev   = StandardDeath.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    val ev1 = StandardDeath.applyToPopulation(
      ev,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    ev1.population should be(97)
    ev1.died should be(3)

  "A probabilistic death " should "behave correctly" in:
    val node = Node.withPopulation(100).withInfected(100).build()
    val ev   = ProbabilisticDeath.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    ev.population should (be >= 80 and be <= 100)

  it should "have a population between 90 and 98 after another probabilistic death event" in:
    val node = Node.withPopulation(100).withInfected(100).build()
    val ev   = ProbabilisticDeath.applyToPopulation(
      node,
      Disease("test", Set(pulmonaryEdema), 1)
    )
    val ev1 = ProbabilisticDeath.applyToPopulation(
      ev,
      Disease("test", Set(pulmonaryEdema), 1)
    )

    ev1.population should (be >= 90 and be <= 98)
