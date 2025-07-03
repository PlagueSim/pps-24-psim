package plague

import model.plague.{Trait, Disease, TraitCategory}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DiseaseTest extends AnyFlatSpec with Matchers {

  // Tratti di esempio
  private val coughing = Trait(
    name = "Coughing",
    category = TraitCategory.Symptom,
    infectivity = 1.0,
    severity = 0.2,
    cost = 3,
    prerequisites = Set.empty
  )
  private val pneumonia = Trait(
    name = "Pneumonia",
    category = TraitCategory.Symptom,
    infectivity = 2.0,
    severity = 0.5,
    lethality = 0.1,
    cost = 4,
    prerequisites = Set(coughing.name)
  )
  private val sneezing = Trait(
    name = "Sneezing",
    category = TraitCategory.Symptom,
    infectivity = 0.5,
    severity = 0.1,
    cost = 2,
    prerequisites = Set(coughing.name)
  )
  private val airTransmission1 = Trait(
    name = "Air Transmission 1",
    category = TraitCategory.Transmission,
    infectivity = 0.1,
    cost = 2,
    prerequisites = Set.empty
  )
  private val airTransmission2 = Trait(
    name = "Air Transmission 2",
    category = TraitCategory.Transmission,
    infectivity = 0.3,
    cost = 5,
    prerequisites = Set(airTransmission1.name)
  )

  "Disease evolution" should "fail if the trait is already evolved" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 10)
    val result = d.evolve(coughing)
    result shouldBe Left("Coughing already evolved.")

  "Disease evolution" should "fail if prerequisites are not met" in:
    val d = Disease(traits = Set.empty, dnaPoints = 10)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Pneumonia is locked. Missing any of: Coughing")

  "Disease evolution" should "fail if not enough DNA points" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 1)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Not enough DNA points to evolve Pneumonia")

  it should "succeed and evolve a new trait when all conditions are met" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 10)
    val result = d.evolve(pneumonia)
    val newDisease = result.toOption.get
    newDisease.traits should contain allOf (coughing, pneumonia)
    newDisease.dnaPoints shouldBe 6

  it should "allow evolving if any one prerequisite is satisfied" in:
    val multiPrereq = Trait("Mutant", TraitCategory.Symptom, prerequisites = Set("Coughing", "Alternative"), cost = 1)

    val d = Disease(traits = Set(coughing), dnaPoints = 5)
    val result = d.evolve(multiPrereq)
    result.isRight shouldBe true

  it should "add DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = 5)
    val updated = d.addDnaPoints(3)
    updated.dnaPoints shouldBe 8

  "Disease evolution" should "remove DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = 10)
    val result = d.evolve(coughing)
    val newDisease = result.toOption.get
    newDisease.dnaPoints shouldBe 7

  "Disease infectivity/severity/lethality" should "sum all trait values correctly" in:
    val d = Disease(traits = Set(coughing, pneumonia), dnaPoints = 0)
    d.infectivity shouldBe (1.0 + 2.0)
    d.severity shouldBe (0.2 + 0.5)
    d.lethality shouldBe (0.0 + 0.1)

  "randomMutation" should "add a new evolvable symptom without consuming DNA points" in {
    val all = Set(coughing, sneezing, pneumonia)

    val disease = Disease(traits = Set(coughing), dnaPoints = 10)
    val mutated = disease.randomMutation(all)

    mutated.dnaPoints shouldBe disease.dnaPoints
    mutated.traits should contain(coughing)
  }

  it should "not mutate if no symptoms are available or evolvable" in {
    val all = Set(pneumonia)

    val disease = Disease(traits = Set.empty, dnaPoints = 10)
    val mutated = disease.randomMutation(all)

    mutated shouldBe disease
  }


}
