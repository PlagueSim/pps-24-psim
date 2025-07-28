package events.InfectionAndDeath

import model.infection.TemperatureAdjuster
import model.infection.TemperatureAdjuster.TemperatureAdjuster
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TemperatureAdjusterTest extends AnyFlatSpec with Matchers:

  private val MIN_TEMPERATURE = 10.0
  private val MAX_TEMPERATURE = 30.0
  private val IDEAL_TEMPERATURE = 20.0
  private val PENALIZED_VALUE = 85.0
  private val UNCHANGED_VALUE_1 = 50.0
  private val UNCHANGED_VALUE_2 = 40.0
  private val UNCHANGED_VALUE_3 = 60.0
  private val STANDARD_VALUE = 100.0
  private val DELTA = 0.01

  given adjuster: TemperatureAdjuster =
    TemperatureAdjuster.defaultTemperatureAdjuster

  "adjustForTemperature" should "reduce value correctly for low temperature" in:
    adjuster.adjustForTemperature(STANDARD_VALUE, 5.0) shouldBe PENALIZED_VALUE +- DELTA

  it should "reduce value correctly for high temperature" in:
    adjuster.adjustForTemperature(STANDARD_VALUE, 35.0) shouldBe PENALIZED_VALUE +- DELTA

  it should "return same value in ideal temperature" in:
    adjuster.adjustForTemperature(UNCHANGED_VALUE_1, IDEAL_TEMPERATURE) shouldBe UNCHANGED_VALUE_1 +- DELTA

  it should "not penalize temperature slightly above min" in:
    adjuster.adjustForTemperature(UNCHANGED_VALUE_2, MIN_TEMPERATURE + DELTA) shouldBe UNCHANGED_VALUE_2 +- DELTA

  it should "not penalize temperature slightly below max" in:
    adjuster.adjustForTemperature(UNCHANGED_VALUE_3, MAX_TEMPERATURE - DELTA) shouldBe UNCHANGED_VALUE_3 +- DELTA
