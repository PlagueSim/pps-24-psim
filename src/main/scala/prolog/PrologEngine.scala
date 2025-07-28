package prolog

import alice.tuprolog
import alice.tuprolog.*

/**
 * Provides a Prolog engine for solving goals and extracting terms.
 */
object PrologEngine:
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")

  /**
   * Extracts a term from a Prolog `Struct` at the specified index.
   */
  def extractTerm(t: Term, i: Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  /**
   * Creates a Prolog engine that can solve goals based on provided clauses.
   */
  def mkPrologEngine(clauses: String*): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(clauses mkString " "))
    goal =>
      new Iterable[Term]:
        override def iterator: Iterator[Term] = new Iterator[Term]:
          var solution: SolveInfo       = engine.solve(goal)
          override def hasNext: Boolean =
            solution != null && (solution.isSuccess || solution.hasOpenAlternatives)
          override def next(): Term =
            if solution == null then
              throw new NoSuchElementException("No more solutions")
            val current = solution.getSolution
            solution =
              if solution.hasOpenAlternatives then engine.solveNext
              else null
            current
      .to(LazyList)
