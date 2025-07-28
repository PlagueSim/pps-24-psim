package model.infection

import alice.tuprolog.{Term, Var, Struct}
import prolog.PrologEngine.*

/**
 * Provides a temperature adjuster that modifies values based on the current temperature.
 * The adjustment is made according to predefined ideal temperature ranges.
 */
object TemperatureAdjuster:
  
    /** Trait defining the interface for temperature adjustment logic.
     */
  trait TemperatureAdjuster:
    def adjustForTemperature(value: Double, temperature: Double): Double

  /** Default implementation of the TemperatureAdjuster.
   */
  given defaultTemperatureAdjuster: TemperatureAdjuster with
    private val idealMin = 10.0
    private val idealMax = 30.0
    private val penalty  = 0.03

    /** Adjusts the given value based on the current temperature.
     *  If the temperature is outside the ideal range, it applies a penalty to the value.
     */
    def adjustForTemperature(
        value: Double,
        temp: Double
    ): Double =
      val prologQuery: Term => LazyList[Term] = mkPrologEngine("""
          match(Temp, IdealMin, IdealMax, Value, Penalty, Res) :-
              Temp < IdealMin,
              Diff is IdealMin - Temp,
              PenaltyFactor is 1 - Diff * Penalty,
              Res is Value * PenaltyFactor.
      
          match(Temp, IdealMin, IdealMax, Value, Penalty, Res) :-
              Temp > IdealMax,
              Diff is Temp - IdealMax,
              PenaltyFactor is 1 - Diff * Penalty,
              Res is Value * PenaltyFactor.
      
          match(Temp, IdealMin, IdealMax, Value, _Penalty, Value) :-
              Temp >= IdealMin,
              Temp =< IdealMax.
        """)

      val temperature = Term.createTerm(temp.toString)
      val idealMinP   = Term.createTerm(idealMin.toString)
      val idealMaxP   = Term.createTerm(idealMax.toString)
      val valueP      = Term.createTerm(value.toString)
      val penalityP   = Term.createTerm(penalty.toString)
      val goal        = Struct(
        "match",
        temperature,
        idealMinP,
        idealMaxP,
        valueP,
        penalityP,
        Var()
      )

      val solutions = prologQuery(goal)
      solutions.head.asInstanceOf[Struct].getArg(5).toString.toDouble
