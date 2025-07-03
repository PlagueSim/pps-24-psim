package model.World

object WorldFactory:
  def mockWorld(): World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()
    val nodeC = Node.withPopulation(8).build()
    val nodeD = Node.withPopulation(8).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB,
      "C" -> nodeC,
      "D" -> nodeD
    )

    val edges = Set(
      Edge("A", "B", EdgeType.Land),
      Edge("A", "B", EdgeType.Air),
      Edge("A", "B", EdgeType.Sea),
      Edge("B", "C", EdgeType.Sea),
      Edge("D", "C", EdgeType.Sea),
      Edge("C", "D", EdgeType.Land),
      Edge("D", "A", EdgeType.Land, weight = 2.0)

    )

    World(
      nodes,
      edges,
      Map(Static -> 1.0)
    )
