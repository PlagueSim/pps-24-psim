package core.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.logic.Logic
import model.core.SimulationState

class DummyLogic extends Logic[Int]:
  override def evolve(state: SimulationState): Int = 42

class LogicTest extends AnyFlatSpec with Matchers:

  "DummyLogic" should "always return 42" in {
    val logic = DummyLogic()
    val state = SimulationState(5)
    logic.evolve(state) shouldEqual 42
  }
