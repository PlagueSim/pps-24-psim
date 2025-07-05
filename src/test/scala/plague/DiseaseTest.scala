package plague
import model.plague.{Disease, Symptoms, Trait}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DiseaseTest extends AnyFlatSpec with Matchers:

  private val nausea = Symptoms.nausea
  private val coughing = Symptoms.coughing
  private val pneumonia = Symptoms.pneumonia
  private val pulmonaryEdema = Symptoms.pulmonaryEdema


  "Disease evolution" should "fail if the trait is already evolved" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 10)
    val result = d.evolve(coughing)
    result shouldBe Left("Coughing already evolved.")

  it should "fail if prerequisites are not met" in:
    val d = Disease(traits = Set.empty, dnaPoints = 10)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Pneumonia is locked.")

  it should "fail if not enough DNA points" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 1)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Not enough DNA points to evolve Pneumonia")

  it should "succeed and evolve a new trait when all conditions are met" in:
    val d = Disease(traits = Set(coughing), dnaPoints = 10)
    val result = d.evolve(pneumonia)
    val newDisease = result.toOption.get
    newDisease.traits should contain allOf (coughing, pneumonia)
    newDisease.dnaPoints shouldBe (d.dnaPoints - pneumonia.cost)

  it should "allow symptom evolution if any one prerequisite is satisfied" in:
    val d = Disease(traits = Set(coughing, pneumonia), dnaPoints = 15)
    val result = d.evolve(pulmonaryEdema)
    result.isRight shouldBe true

  it should "remove DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = 10)
    val result = d.evolve(coughing)
    val newDisease = result.toOption.get
    newDisease.dnaPoints shouldBe (d.dnaPoints - coughing.cost)

  "addDnaPoints" should "add DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = 5)
    val dnaToAdd = 3
    val updated = d.addDnaPoints(dnaToAdd)
    updated.dnaPoints shouldBe (d.dnaPoints + dnaToAdd)

  "Disease infectivity/severity/lethality" should "sum all trait values correctly" in:
    val d = Disease(traits = Set(coughing, nausea), dnaPoints = 0)
    d.infectivity shouldBe (coughing.infectivity + nausea.infectivity) //Il compilatore mente
    d.severity shouldBe (coughing.severity + nausea.severity)
    d.lethality shouldBe (coughing.lethality + nausea.lethality)

  "randomMutation" should "add a new evolvable symptom without consuming DNA points" in:
    val all = Symptoms.allBasics
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = 10)
    val mutated = disease.randomMutation(all)
    mutated.dnaPoints shouldBe disease.dnaPoints
    mutated.traits should contain(coughing)

  it should "not mutate if no symptoms are available or evolvable" in:
    val all = Set(pneumonia)
    val disease = Disease(traits = Set.empty, dnaPoints = 10)
    val mutated = disease.randomMutation(all)
    mutated shouldBe disease

