package model.world.prolog

import alice.tuprolog.{Struct, Term}
import model.world.{Edge, World}
import prolog.PrologEngine.mkPrologEngine

object WorldConnectivity:

  private def generatePrologEdges(edges: Map[String, Edge]): String =
    edges.values.map(e => s"edge('${e.nodeA}', '${e.nodeB}').").mkString("\n")

  private val connectivityRules: String =
    """
    connected(A, B) :- edge(A, B).
    connected(A, B) :- edge(B, A).
    """

  /**
   * Checks if two nodes in the world are connected via edges.
   * @param world the World object containing nodes and edges
   * @param from the ID of the starting node
   * @param to the ID of the target node
   * @return
   */
  def areConnected(world: World, from: String, to: String): Boolean =
    val prologFacts = generatePrologEdges(world.edges)
    val fullProgram = s"$prologFacts\n$connectivityRules"
    val prologQuery = mkPrologEngine(fullProgram)
    val goal = Struct("connected", Term.createTerm(s"'$from'"), Term.createTerm(s"'$to'"))
    prologQuery(goal).nonEmpty
