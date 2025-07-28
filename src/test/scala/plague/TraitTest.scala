package plague

import model.plague.db.Symptoms
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TraitTest extends AnyFlatSpec with Matchers:
  private val nausea = Symptoms.nausea
  private val coma = Symptoms.coma

  "a Trait" should "be root if it has no prerequisites" in:
    nausea.isRoot shouldBe true

  it should "NOT be considered root otherwise" in :
    coma.isRoot shouldBe false

