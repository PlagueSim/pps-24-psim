package plague

import model.world.{Edge, MovementStrategy, Node, Static, World}
import model.core.{SimulationEngine, SimulationState}
import model.cure.Cure
import model.plague.{Disease, Symptoms, Trait}
import model.plague.Symptoms.*
import model.plague.Abilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.plague.DiseaseEvents.*
import model.events.plague.DnaPointsCalculator
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.EdgeType.*

class DiseaseEventsTest extends AnyFlatSpec with Matchers:
  private val movements: Map[MovementStrategy, Double] = Map(
    Static -> 1.0
  )
  private val nodes = Map(
    "Italy" -> Node.Builder(100, 0, 0).build(),
    "France" -> Node.Builder(100, 1, 0).build()
  )
  private val edges = Map(
    "Ita-Fra" -> Edge("Italy", "France", Air)
  )
  private val dna = 1000
  private val baseCureProgress = 0.5
  private val baseCureSpeed = 0.1
  private val simState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    Disease("Pax-12", Set.empty, dna),
    Cure(baseCureProgress, baseCureSpeed),
    World(nodes, edges, movements),
    null,
    null,
    null
  )

  "Evolution Event" should "evolve the the specified trait" in:
    val evolvedDisease = Disease("Pax-12", Set(coughing), dna - coughing.stats.cost)
    val nextState = SimulationEngine.executeEvent(Evolution(coughing)).run(simState).value._1
    nextState.disease shouldBe evolvedDisease

  it should "not evolve anything if the Trait cannot be evolved" in:
    val nextState = SimulationEngine.executeEvent(Evolution(totalOrganFailure)).run(simState).value._1
    nextState.disease shouldBe simState.disease

  "Involution Event" should "remove an evolved trait if possible" in:
    val evolvedState = SimulationEngine.executeEvent(Evolution(coughing)).run(simState).value._1
    val backToNormal = SimulationEngine.executeEvent(Involution(coughing)).run(evolvedState).value._1
    backToNormal.disease shouldBe Disease("Pax-12", Set.empty, dna - coughing.stats.cost + 2)

  it should "not remove anything if the Trait cannot be involved" in:
    val evolvedState = SimulationEngine.executeEvent(Evolution(coughing)).run(simState).value._1
    val involved = SimulationEngine.executeEvent(Involution(totalOrganFailure)).run(evolvedState).value._1
    involved.disease shouldBe evolvedState.disease

  "DnaPointsCalculator" should "calculate DNA points correctly when new infections and deaths occur" in :
    val prevNodes = nodes

    val currentNodes = Map(
      "Italy" -> Node.Builder(100, 1, 0).build(),
      "France" -> Node.Builder(100, 100, 100).build()
    )

    val expectedResult = DnaPointsCalculator.calculate(prevNodes, currentNodes)
    val actualResult =
      SimulationEngine.executeEvent(DnaPointsAddition(currentNodes)).run(simState).value._2.dnaPoints -
        simState.disease.dnaPoints

    expectedResult shouldBe actualResult

  "Mutation event" should "mutate when the mutation chance is higher than a randomly chosen double" in:
    val mutatedDisease = SimulationEngine.executeEvent(Mutation(() => 0.0)).run(simState).value._2
    mutatedDisease should not be simState.disease

  it should "not mutate when the mutation chance is below the mutation chance" in:
    val notMutatedDisease = SimulationEngine.executeEvent(Mutation(() => 2.0)).run(simState).value._2
    notMutatedDisease shouldBe simState.disease

  "Cure Slowdown Event" should "reduce Cure speed" in:
    val updatedCure = SimulationEngine.executeEvent(CureSlowDown(geneticHardening1)).run(simState).value._2
    updatedCure.effectiveSpeed shouldBe Math.max(0, simState.cure.effectiveSpeed - geneticHardening1.stats.cureSlowdown)

  "Cure Pushback Event" should "reduce Cure progress" in:
    val updatedCure = SimulationEngine.executeEvent(CurePushBack(geneticReShuffle1)).run(simState).value._2
    updatedCure.progress shouldBe Math.max(0, simState.cure.progress - geneticReShuffle1.stats.cureReset)

  "RemoveCureModifier" should "remove the specified Trait modifiers applied to Cure" in:
    val updatedCureSimState = SimulationEngine.executeEvent(CurePushBack(geneticHardening1)).run(simState).value._1
    val removedCureMod = SimulationEngine.executeEvent(RemoveCureModifier(geneticHardening1)).run(updatedCureSimState).value._2
    removedCureMod shouldBe simState.cure