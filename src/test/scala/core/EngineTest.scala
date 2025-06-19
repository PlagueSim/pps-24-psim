package core

import model.core.SimulationState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EngineTest extends AnyFlatSpec with Matchers {

  "The simulationState" should "contains all the vital component of the simulation, such as the World, the Virus," +
    "the Days" in {
    val simState = SimulationState(0)
    simState.currentDay shouldBe a [Int]
  }


}
