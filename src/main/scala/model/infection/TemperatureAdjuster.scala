package model.infection

object TemperatureAdjuster:
  trait Rounding:
    def round(value: Double): Int

  trait TemperatureAdjuster:
    def adjustForTemperature(value: Double, temperature: Double): Double

  given defaultTemperatureAdjuster: TemperatureAdjuster with
    private val idealMin = 10.0
    private val idealMax = 30.0
    private val penalty  = 0.03

    def adjustForTemperature(
        value: Double,
        temp: Double
    ): Double =
      temp match
        case low if temp < idealMin => value * (1 - (idealMin - temp) * penalty)
        case high if temp > idealMax =>
          value * (1 - (temp - idealMax) * penalty)
        case _ => value
