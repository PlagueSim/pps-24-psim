package model.reaction

import model.reaction.ReactionAction.CloseEdges
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.EdgeType
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReactionsTest extends AnyFlatSpec with Matchers:
  def defaultRule: ReactionRule = ReactionRule(
    InfectedCondition(threshold = 0.2),
    (nodeId) => CloseEdges(EdgeType.Land, nodeId)
  )

  "Reactions" should "add active reactions correctly" in:
    val initialReactions = Reactions()
    initialReactions.activeReactions shouldBe empty
    val newReactions = List(
      ActiveReaction(defaultRule, "A", BasicYear(Day(1), Year(2023)))
    )
    val updatedReactions = initialReactions.addActive(newReactions)
    updatedReactions.activeReactions should have size 1
