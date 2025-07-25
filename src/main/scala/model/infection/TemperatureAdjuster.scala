package model.infection

import alice.tuprolog.{Term, Var, Struct}
import prolog.PrologEngine.*

object TemperatureAdjuster:

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
      println(solutions.head.asInstanceOf[Struct].getArg(5).toString.toDouble)
      solutions.head.asInstanceOf[Struct].getArg(5).toString.toDouble
//      temp match
//        case low if temp < idealMin => value * (1 - (idealMin - temp) * penalty)
//        case high if temp > idealMax =>
//          value * (1 - (temp - idealMax) * penalty)
//        case _ => value
