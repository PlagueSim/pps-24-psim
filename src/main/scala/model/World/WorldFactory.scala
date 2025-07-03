package model.World

object WorldFactory:
  def mockWorld(): World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()
    val nodeC = Node.withPopulation(8).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB,
      "C" -> nodeC
    )

    val edges = Set(
      Edge("A", "B", EdgeType.Land),
      Edge("A", "B", EdgeType.Air),
      Edge("A", "B", EdgeType.Sea),
      Edge("B", "C", EdgeType.Sea)
    )

    World(
      nodes,
      edges,
      Map(Static -> 1.0)
    )
