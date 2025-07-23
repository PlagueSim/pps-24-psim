package plague

import model.world.{MovementStrategy, Static, World}
import model.core.{SimulationState, SimulationEngine}
import model.cure.Cure
import model.plague.{Disease, Symptoms, Trait}
import model.plague.Symptoms.*
import model.plague.Abilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.DiseaseEvents.*
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}

class DiseaseEventsTest extends AnyFlatSpec with Matchers:
  private val movements: Map[MovementStrategy, Double] = Map(
    Static -> 1.0
  )
  private val dna = 1000
  private val baseCureProgress = 0.5
  private val baseCureSpeed = 0.1
  private val simState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    Disease("Pax-12", Set.empty, dna),
    Cure(baseCureProgress, baseCureSpeed),
    World(Map.empty, Set.empty, movements),
    null,
    null,
    null
  )

  "Evolution Event" should "evolve the the specified trait" in:
    val evolvedDisease = Disease("Pax-12", Set(coughing), dna - coughing.stats.cost)
    val nextState = SimulationEngine.executeEvent(Evolution(coughing)).run(simState).value._1
    nextState.disease shouldBe evolvedDisease
    
  "Involution Event" should "remove an evolved trait if possible" in:
    val evolvedState = SimulationEngine.executeEvent(Evolution(coughing)).run(simState).value._1
    val backToNormal = SimulationEngine.executeEvent(Involution(coughing)).run(evolvedState).value._1
    backToNormal.disease shouldBe Disease("Pax-12", Set.empty, dna - coughing.stats.cost + 2)
    
  //todo
  "Cure Slowdown Event" should "reduce Cure speed" in:
    val nextState = SimulationEngine.executeEvent(CureSlowDown(geneticHardening1)).run(simState).value._1
    val newCure = nextState.cure.advance()
    newCure.baseSpeed shouldBe Math.max(0, simState.cure.baseSpeed - geneticHardening1.stats.cureSlowdown)

  //todo
  "Cure Pushback Event" should "reduce Cure progress" in:
    val nextState = SimulationEngine.executeEvent(CureSlowDown(geneticHardening1)).run(simState).value._1
    val newCure = nextState.cure.advance()
    newCure.baseSpeed shouldBe Math.max(0, simState.cure.baseSpeed - geneticHardening1.stats.cureSlowdown)

  //todo
  "RemoveCureModifier" should "remove the specified Trait modifiers applied to Cure" in:
    val nextState = SimulationEngine.executeEvent(CureSlowDown(geneticHardening1)).run(simState).value._1
    val newCure = nextState.cure.advance()
    newCure.baseSpeed shouldBe Math.max(0, simState.cure.baseSpeed - geneticHardening1.stats.cureSlowdown)