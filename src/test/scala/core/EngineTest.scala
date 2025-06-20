package core

import model.core.{SimulationEngine, SimulationState}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.*

class EngineTest extends AnyFlatSpec with Matchers {

  "The simulationState" should "contains all the vital component of the simulation, such as the World, the Virus," +
    "the Days" in {
      val simState = SimulationState(0)
      simState.currentDay shouldBe a[Int]
    }

  it should "be able to read the current day" in {
    val simState = SimulationState(5)
    simState.currentDay shouldEqual 5
  }

  "The engine" should "be able to execute a simulation step and returning the updated currentDay value" in {
    val simState  = SimulationState(0)
    val nextState =
      for nextState <- SimulationEngine.executeEvent(AdvanceDayEvent())
      yield nextState
    nextState.run(simState).value._2 shouldEqual 1
  }

  "An event " should "have the execute method" in {
    val simState                    = SimulationState(0)
    val advanceDayEvent: Event[Int] = AdvanceDayEvent()
    val nextState = SimulationEngine.executeEvent(advanceDayEvent)
    nextState.run(simState).value._2 shouldEqual 1
  }

  it should "be able to call execute method and return the updated currentDay value" in {
    val simState                    = SimulationState(0)
    val advanceDayEvent: Event[Int] = AdvanceDayEvent()
    val nextState                   = for
      s1 <- SimulationEngine.executeEvent(advanceDayEvent)
      s2 <- SimulationEngine.executeEvent(advanceDayEvent)
    yield s2
    nextState.run(simState).value._2 shouldEqual 2
  }

  "The simulation engine" should "be able to run a standard simulation" in {
    SimulationEngine.runStandardSimulation()
  }

  it should "print the correct end day" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      SimulationEngine.runStandardSimulation()
    }
    stream.toString should include("Simulation ended on day: 6")
  }

}
