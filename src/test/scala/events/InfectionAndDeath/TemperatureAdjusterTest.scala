package events.InfectionAndDeath

import model.infection.TemperatureAdjuster
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TemperatureAdjusterTest extends AnyFlatSpec with Matchers:

  given adjuster: TemperatureAdjuster =
    TemperatureAdjuster.defaultTemperatureAdjuster

  "adjustForTemperature" should "reduce value correctly for low temperature" in:
    adjuster.adjustForTemperature(100.0, 5.0) shouldBe 85.0 +- 0.01

  it should "reduce value correctly for high temperature" in:
    adjuster.adjustForTemperature(100.0, 35.0) shouldBe 85.0 +- 0.01

  it should "return same value in ideal temperature" in:
    adjuster.adjustForTemperature(50.0, 20.0) shouldBe 50.0 +- 0.01

  it should "not penalize temperature slightly above min" in:
    adjuster.adjustForTemperature(40.0, 10.1) shouldBe 40.0 +- 0.01

  it should "not penalize temperature slightly below max" in:
    adjuster.adjustForTemperature(60.0, 29.9) shouldBe 60.0 +- 0.01
