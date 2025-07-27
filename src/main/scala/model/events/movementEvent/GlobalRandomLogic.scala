package model.events.movementEvent

import model.world.Node

object GlobalRandomLogic extends MovementLogic:

  def compute(
               nodes: Map[String, Node],
               percent: Double,
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String, Int)] =
    val totalPopulation = nodes.values.map(_.population).sum
    val peopleToMove = (totalPopulation * percent).toInt
    if peopleToMove == 0 then return List.empty
    val eligibleSources = nodes.filter(_._2.population > 0).keys.toVector
    val assigned = assignPeopleToSources(nodes, eligibleSources, peopleToMove, rng)
    assigned.toList
      .filter((from, _) => neighbors(from).exists(isEdgeOpen(from, _)))
      .flatMap(generateMovesFromSource(_, neighbors, isEdgeOpen, rng))

  private def assignPeopleToSources(
                                     nodes: Map[String, Node],
                                     sources: Vector[String],
                                     peopleToMove: Int,
                                     rng: scala.util.Random
                                   ): Map[String, Int] =
    LazyList
      .continually(sources(rng.nextInt(sources.size)))
      .filter(id => nodes(id).population > 0)
      .scanLeft(Map.empty[String, Int].withDefaultValue(0)) { (acc, id) =>
        if acc(id) < nodes(id).population then acc.updated(id, acc(id) + 1)
        else acc
      }
      .dropWhile(_.values.sum < peopleToMove)
      .head

  private def generateMovesFromSource(
                                       entry: (String, Int),
                                       neighbors: String => Set[String],
                                       isEdgeOpen: (String, String) => Boolean,
                                       rng: scala.util.Random
                                     ): List[(String, String, Int)] =
    val (from, count) = entry
    val openDestinations = neighbors(from).filter(isEdgeOpen(from, _)).toVector

    if openDestinations.isEmpty then List.empty
    else List.fill(count) {
      val to = openDestinations(rng.nextInt(openDestinations.size))
      (from, to, count)
    }
