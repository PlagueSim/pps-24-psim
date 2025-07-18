package plague

import model.world.{MovementStrategy, Static, World}
import model.core.{SimulationState, SimulationEngine}
import model.cure.Cure
import model.plague.{Disease, Symptoms, Trait}
import model.plague.Symptoms.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.DiseaseEvents.*
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}

class DiseaseEventsTest extends AnyFlatSpec with Matchers:
  private val movements: Map[MovementStrategy, Double] = Map(
    Static -> 1.0
  )
  private val simState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    Disease("Pax-12", Set.empty, 20),
    Cure(),
    World(Map.empty, Set.empty, movements),
    null,
    null
  )

  "This random test" should "print what it is told to print lol" in:
    val evolvedDisease = Disease("Pax-12", Set(coughing), 20 - coughing.stats.cost)
    val nextState = SimulationEngine.executeEvent(Evolution(coughing))
    nextState.run(simState).value._2 shouldBe evolvedDisease