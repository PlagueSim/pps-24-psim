package core

import model.core.{SimulationEngine, SimulationState}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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
      for nextState <- SimulationEngine.advanceDay()
      yield nextState
    nextState.run(simState).value._2 shouldEqual 1
  }

  it should "be able to execute multiple simulation steps" in {
    val simState            = SimulationState(0)
    val advanceMultipleDays = SimulationEngine.advanceFor(4)
    advanceMultipleDays.run(simState).value._2 shouldEqual 4
  }

}
