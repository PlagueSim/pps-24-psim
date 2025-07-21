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
    nodeId => CloseEdges(EdgeType.Land, nodeId),
    duration = Some(5)
  )

  "Reactions" should "add active reactions correctly" in:
    val initialReactions = Reactions()
    initialReactions.activeReactions shouldBe empty
    val newReactions = Set(
      ActiveReaction(defaultRule, "A", BasicYear(Day(1), Year(2023)))
    )
    val updatedReactions = initialReactions.addActive(newReactions)
    updatedReactions.activeReactions should have size 1

  it should "remove expired reactions correctly" in:
    val initialReactions = Reactions(
      activeReactions = Set(
        ActiveReaction(defaultRule, "A", BasicYear(Day(1), Year(2023))),
        ActiveReaction(defaultRule, "B", BasicYear(Day(2), Year(2023)))
      )
    )
    initialReactions.activeReactions should have size 2
    val currentDay = BasicYear(Day(6), Year(2023)) // The second reaction should still be active
    val updatedReactions = initialReactions.removeExpired(currentDay)
    updatedReactions.activeReactions should have size 1

  it should "not add any reactions if given an empty set" in:
    val initialReactions = Reactions()
    val updatedReactions = initialReactions.addActive(Set.empty)
    updatedReactions.activeReactions shouldBe empty

  it should "not remove any reactions if none are expired" in:
    val initialReactions = Reactions(
      activeReactions = Set(
        ActiveReaction(defaultRule, "A", BasicYear(Day(1), Year(2023)))
      )
    )
    val currentDay = BasicYear(Day(2), Year(2023))
    val updatedReactions = initialReactions.removeExpired(currentDay)
    updatedReactions.activeReactions should have size 1

  it should "remove all reactions if all are expired" in:
    val initialReactions = Reactions(
      activeReactions = Set(
        ActiveReaction(defaultRule, "A", BasicYear(Day(1), Year(2023))),
        ActiveReaction(defaultRule, "B", BasicYear(Day(1), Year(2023)))
      )
    )
    val currentDay = BasicYear(Day(10), Year(2023))
    val updatedReactions = initialReactions.removeExpired(currentDay)
    updatedReactions.activeReactions shouldBe empty
