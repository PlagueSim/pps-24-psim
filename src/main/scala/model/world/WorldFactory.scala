package model.world

object WorldFactory:

  def mockWorld(): World =
    val nodes =
      (1 to 15).map { i =>
        val id = ('A' + (i - 1)).toChar.toString
        id -> Node.withPopulation(10 + i).withDied(2).build()
      }.toMap

    val edges = Set(
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
      Edge("J", "K", EdgeType.Land),
      Edge("K", "L", EdgeType.Sea),
      Edge("L", "M", EdgeType.Air),
      Edge("M", "N", EdgeType.Land),
      Edge("N", "O", EdgeType.Sea),
      Edge("O", "A", EdgeType.Air), // wrap around to A
      // Some extra cross connections:
      Edge("A", "H", EdgeType.Sea),
      Edge("C", "I", EdgeType.Land),
      Edge("D", "K", EdgeType.Air).close,
      Edge("F", "M", EdgeType.Sea),
      Edge("G", "O", EdgeType.Land)
    )

    World(
      nodes,
      edges,
      Map(Static -> 0.6, RandomNeighbor -> 0.4)
    )
