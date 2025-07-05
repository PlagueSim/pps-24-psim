package plague

import model.plague.Symptoms
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TraitTest extends AnyFlatSpec with Matchers:
  private val nausea = Symptoms.nausea
  private val coma = Symptoms.coma

  "a Trait without prerequisites" should "be considered root" in:
    nausea.isRoot() shouldBe true

  "a Trait with prerequisites" should "NOT be considered root" in :
    coma.isRoot() shouldBe false

