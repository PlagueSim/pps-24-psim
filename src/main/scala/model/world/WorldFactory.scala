package model.world

object WorldFactory:

  def mockWorld(): World =
    val nodes =
      (1 to 15).map { i =>
        val id = ('A' + (i - 1)).toChar.toString
        val baseBuilder = Node.withPopulation(10 + i).withDied(2)

        val builderWithInfection = id match
          case "A" => baseBuilder.withInfected(5)
          case "C" => baseBuilder.withInfected(3)
          case "F" => baseBuilder.withInfected(6)
          case "J" => baseBuilder.withInfected(4)
          case "O" => baseBuilder.withInfected(7)
          case _   => baseBuilder

        id -> builderWithInfection.build()
      }.toMap

    val edgeSet: Set[Edge] = Set(
      Edge("A", "C", EdgeType.Land),
      Edge("A", "B", EdgeType.Land),
      Edge("B", "C", EdgeType.Sea),
      Edge("C", "D", EdgeType.Air),
      Edge("D", "E", EdgeType.Land),
      Edge("E", "F", EdgeType.Sea),
      Edge("F", "G", EdgeType.Air),
      Edge("G", "H", EdgeType.Land),
      Edge("H", "I", EdgeType.Sea),
      Edge("I", "J", EdgeType.Air),
      Edge("J", "K", EdgeType.Land).close,
      Edge("K", "L", EdgeType.Sea).close,
      Edge("L", "M", EdgeType.Air),
      Edge("M", "N", EdgeType.Land),
      Edge("N", "O", EdgeType.Sea),
      Edge("O", "A", EdgeType.Air), // wrap around to A
      Edge("A", "H", EdgeType.Sea),
      Edge("C", "I", EdgeType.Land),
      Edge("D", "K", EdgeType.Air).close,
      Edge("F", "M", EdgeType.Sea),
      Edge("G", "O", EdgeType.Land)
    )

    val edgeMap: Map[String, Edge] = edgeSet.map { edge =>
      val id = edgeId(edge.nodeA, edge.nodeB, edge.typology)
      id -> edge
    }.toMap

    World(
      nodes,
      edgeMap,
      Map(Static -> 0.6, RandomNeighbor -> 0.4)
    )

  private def edgeId(a: String, b: String, typology: EdgeType): String =
    val sorted = if a < b then s"$a-$b" else s"$b-$a"
    s"$sorted-${typology.toString}"
