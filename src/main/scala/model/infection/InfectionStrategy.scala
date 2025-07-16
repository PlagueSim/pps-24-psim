//package model.infection
//
//import model.plague.Disease
//import model.world.Node
//import scala.util.Random
//
//object InfectionStrategy:
//
//  trait Infection:
//    def calculateInfection(node: Node, disease: Disease): Node
//
//  private case class FunctionalInfectionStrategy(
//      adjustInfectivity: Double => Double,
//      spreadFunction: (Int, Double) => Int
//  ) extends Infection:
//
//    def calculateInfection(node: Node, disease: Disease): Node =
//      val healthy = node.population - node.infected
//      if healthy <= 0 || node.infected == 0 then node
//      else
//        val pressure      = node.infected.toDouble / node.population
//        val infectivity   = adjustInfectivity(disease.infectivity)
//        val probability   = infectivity * pressure
//        val newInfections = spreadFunction(healthy, probability)
//        node.applyInfection(newInfections)
//
//  val StandardInfection: Infection = FunctionalInfectionStrategy(
//    adjustInfectivity = infectivity => infectivity,
//    spreadFunction = (healthy, probability) => (healthy * probability).toInt
//  )
//
//  def standardTemperatureAwareInfection(temp: Double): Infection =
//    FunctionalInfectionStrategy(
//      adjustInfectivity =
//        infectivity => applyTemperatureVariation(infectivity, temp),
//      spreadFunction = (healthy, probability) => (healthy * probability).toInt
//    )
//
//  val ProbabilisticInfection: Infection = FunctionalInfectionStrategy(
//    adjustInfectivity = infectivity => infectivity,
//    spreadFunction =
//      (healthy, probability) => (1 to healthy).count(_ => Random.nextDouble() < probability)
//  )
//
//  def ProbabilisticTemperatureInfection(temp: Double): Infection =
//    FunctionalInfectionStrategy(
//      adjustInfectivity =
//        infectivity => applyTemperatureVariation(infectivity, temp),
//      spreadFunction =
//        (healthy, probability) => (1 to healthy).count(_ => Random.nextDouble() < probability)
//    )
//
//  private def applyTemperatureVariation(
//      infectivity: Double,
//      temperature: Double
//  ): Double =
//    val idealMinTemp     = 10.0
//    val idealMaxTemp     = 30.0
//    val penaltyPerDegree = 0.03
//
//    temperature match
//      case low if temperature < idealMinTemp =>
//        val penalty = (idealMinTemp - temperature) * penaltyPerDegree
//        (infectivity * (1.0 - penalty)).max(0)
//      case high if temperature > idealMaxTemp =>
//        val penalty = (temperature - idealMaxTemp) * penaltyPerDegree
//        (infectivity * (1.0 - penalty)).max(0)
//      case _ => infectivity
