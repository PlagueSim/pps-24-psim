package core

import model.core.{SimulationEngine, SimulationState}
import model.cure.Cure
import model.world.{MovementStrategy, Static, World}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.*
import model.plague.Disease
import model.time.Time
import model.time.BasicYear
import model.time.TimeTypes.*

class EngineTest extends AnyFlatSpec with Matchers:

  private val DAY_ZERO = 0
  private val DAY_ONE = DAY_ZERO + 1
  private val DAY_TWO = DAY_ONE + 1
  private val STARTING_NUMBER_OF_INFECTED = 1
  private val INFECTED_PER_NODE = 5
  
  val state: SimulationState = SimulationState.createStandardSimulationState()

  private val NUMBER_OF_NODES = state.world.nodes.size
  
  "The simulationState" should "contains all the vital component of the simulation, such as the World, the Virus," +
    "the Days" in {
      val simState = state
      simState.time shouldBe a[Time]
    }

  it should "be able to read the current day" in {
    val simState = state
    simState.time.day.value shouldEqual DAY_ZERO
  }

  "The engine" should "be able to execute a simulation step and returning the updated time value" in:
    val simState  = state
    val nextState =
      for nextState <- SimulationEngine.executeEvent(AdvanceDayEvent())
      yield nextState
    nextState.run(simState).value._2.day.value shouldEqual DAY_ONE

  "An event " should "have the execute method" in:
    val simState = state
    val advanceDayEvent: Event[Time] = AdvanceDayEvent()
    val nextState = SimulationEngine.executeEvent(advanceDayEvent)
    nextState.run(simState).value._2.day.value shouldEqual DAY_ONE

  it should "be able to call execute method and return the updated currentDay value" in:
    val simState = state
    val advanceDayEvent: Event[Time] = AdvanceDayEvent()
    val nextState                    = for
      s1 <- SimulationEngine.executeEvent(advanceDayEvent)
      s2 <- SimulationEngine.executeEvent(advanceDayEvent)
    yield s2
    nextState.run(simState).value._2.day.value shouldEqual DAY_TWO

  val newState: SimulationState = SimulationEngine.runStandardSimulation(state)

  "The simulation engine" should "be able to run a standard simulation and correctly advance the date" in:
    newState.time.day.value shouldEqual DAY_ONE

  it should "start infecting from the first tick" in:
    newState.world.nodes.values.map(x => x.infected).sum should be > STARTING_NUMBER_OF_INFECTED

  it should "have 5 (1 initial + 4 just infected) infected per nodes after the first tick" in:
    newState.world.nodes.values.foreach(x => x.infected shouldEqual INFECTED_PER_NODE)

  it should "have in total 10 infected after the first tick" in:
    newState.world.nodes.values.map(x => x.infected).sum shouldEqual (INFECTED_PER_NODE * NUMBER_OF_NODES)

  it should "not advance the cure instantly" in:
    newState.cure.progress shouldEqual 0

  it should "not have any active reactions after the first tick" in:
    newState.reactions.activeReactions shouldBe empty

  it should "have the standard disease, and not mutated one" in:
    newState.disease shouldEqual state.disease

  it should "not have any deaths" in:
    newState.world.nodes.values.map(x => x.died).sum shouldEqual 0
