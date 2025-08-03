package plague
import model.plague.{Disease, Trait}
import model.plague.traits.Symptoms.*
import model.plague.traits.Symptoms
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DiseaseTest extends AnyFlatSpec with Matchers:

  private val ENOUGH_DNA_POINTS = 100
  private val NOT_ENOUGH_DNA_POINTS = 0

  "Disease evolution" should "fail if the trait is already evolved" in:
    val d = Disease(traits = Set(coughing), dnaPoints = ENOUGH_DNA_POINTS)
    val result = d.evolve(coughing)
    result shouldBe Left("Coughing already evolved.")

  it should "fail if trait prerequisites are not met" in:
    val d = Disease(traits = Set.empty, dnaPoints = ENOUGH_DNA_POINTS)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Pneumonia is locked.")

  it should "fail if not enough DNA points" in:
    val d = Disease(traits = Set(coughing), dnaPoints = NOT_ENOUGH_DNA_POINTS)
    val result = d.evolve(pneumonia)
    result shouldBe Left("Not enough DNA points to evolve Pneumonia")

  it should "succeed and evolve a new trait when all conditions are met" in:
    val d = Disease(traits = Set(coughing), dnaPoints = ENOUGH_DNA_POINTS)
    val result = d.evolve(pneumonia)
    val newDisease = result.toOption.get
    newDisease.traits should contain allOf (coughing, pneumonia)
    newDisease.dnaPoints shouldBe (d.dnaPoints - pneumonia.stats.cost)

  it should "allow symptom evolution if any one prerequisite is satisfied" in:
    val d = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    val result = d.evolve(pulmonaryEdema)
    result.isRight shouldBe true

  it should "remove DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = ENOUGH_DNA_POINTS)
    val result = d.evolve(coughing)
    val newDisease = result.toOption.get
    newDisease.dnaPoints shouldBe (d.dnaPoints - coughing.stats.cost)

  "addDnaPoints" should "add DNA points correctly" in:
    val d = Disease(traits = Set.empty, dnaPoints = ENOUGH_DNA_POINTS)
    val dnaToAdd = 3
    val updated = d.addDnaPoints(dnaToAdd)
    updated.dnaPoints shouldBe (d.dnaPoints + dnaToAdd)

  "Disease infectivity/severity/lethality" should "sum all trait values correctly" in:
    val d = Disease(traits = Set(coughing, nausea), dnaPoints = ENOUGH_DNA_POINTS)
    d.infectivity should be (coughing.stats.infectivity + nausea.stats.infectivity)
    d.severity should be (coughing.stats.severity + nausea.stats.severity)
    d.lethality should be (coughing.stats.lethality + nausea.stats.lethality)

  "randomMutation" should "add a new evolvable symptom" in:
    val all = Symptoms.allBasics.toSet
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    val (_, mutated) = disease.randomMutation()
    mutated.traits should contain(coughing)

  it should "not consume DNA points" in:
    val all = Symptoms.allBasics.toSet
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    val (_, mutated) = disease.randomMutation()
    mutated.dnaPoints shouldBe disease.dnaPoints

  "Disease involution" should "fail if the trait was not evolved" in:
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    disease.involve(nausea) shouldBe Left(s"${nausea.name} is not currently evolved.")

  it should "fail if involving the trait would leave any other isolated" in:
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    disease.involve(coughing) shouldBe Left(s"${coughing.name} cannot be removed because other traits depend on it.")

  it should "remove the specified trait otherwise" in:
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    val involved = disease.involve(pneumonia).toOption.get
    involved.traits should contain only coughing

  it should "refund DNA points" in:
    val disease = Disease(traits = Set(coughing, pneumonia), dnaPoints = ENOUGH_DNA_POINTS)
    val involved = disease.involve(pneumonia).toOption.get
    involved.dnaPoints shouldBe (disease.dnaPoints + disease.refund)

  it should "involve correctly" in:
    val disease = Disease(traits = Set(coughing, pneumonia, pulmonaryEdema, vomiting, nausea), dnaPoints = ENOUGH_DNA_POINTS)
    val involved = disease.involve(vomiting).toOption.get

    involved.traits should not contain vomiting

  it should "fail evolving" in:
    val disease = Disease(traits = Set(coughing, pulmonaryEdema, vomiting, nausea), dnaPoints = ENOUGH_DNA_POINTS)
    disease.involve(vomiting) shouldBe Left(s"${vomiting.name} cannot be removed because other traits depend on it.")



