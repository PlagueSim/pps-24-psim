package World

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodeTest extends AnyFlatSpec with Matchers:

  import model.World.Node

  import model.World.Node.*

  "NodeBuilder" should "build a node with only population" in {
    val node = Node.withPopulation(100).build()
    node.population shouldBe 100
    node.infected shouldBe 0
    node.cureEffectiveness shouldBe 0.0
  }

  it should "build a node with population and infected" in {
    val node = Node.withPopulation(100).withInfected(25).build()
    node.population shouldBe 100
    node.infected shouldBe 25
  }

  it should "build a node with population, infected, and cureEffectiveness" in {
    val node = Node.withPopulation(100).withInfected(50).withCureEffectiveness(0.3).build()
    node.population shouldBe 100
    node.infected shouldBe 50
    node.cureEffectiveness shouldBe 0.3
  }

  "infectedPercentage" should "return 0.0 when population is 0" in {
    val node = Node.withPopulation(0).build()
    node.infectedPercentage() shouldBe 0.0
  }

  it should "return correct value when population > 0" in {
    val node = Node.withPopulation(100).withInfected(25).build()
    node.infectedPercentage() shouldBe 0.25
  }

  "applyCure" should "reduce infected by cureEffectiveness proportion" in {
    val node = Node.withPopulation(100).withInfected(40).withCureEffectiveness(0.5).build()
    val cured = node.applyCure()
    cured.infected shouldBe 20
  }

  it should "not reduce infected below 0" in {
    val node = Node.withPopulation(100).withInfected(10).withCureEffectiveness(1.0).build()
    val cured = node.applyCure()
    cured.infected shouldBe 0
  }

  "applyInfection" should "increase infected without exceeding population" in {
    val node = Node.withPopulation(100).withInfected(60).build()
    val infected = node.applyInfection(50)
    infected.infected shouldBe 100
  }

  "heal" should "reduce infected by given count" in {
    val node = Node.withPopulation(100).withInfected(30).build()
    val healed = node.heal(10)
    healed.infected shouldBe 20
  }

  it should "not allow infected to go below 0" in {
    val node = Node.withPopulation(100).withInfected(5).build()
    val healed = node.heal(10)
    healed.infected shouldBe 0
  }

  "increasePopulation" should "increase the total population" in {
    val node = Node.withPopulation(50).build()
    val increased = node.increasePopulation(20)
    increased.population shouldBe 70
  }

  "decreasePopulation" should "reduce population correctly" in {
    val node = Node.withPopulation(80).build()
    val decreased = node.decreasePopulation(30)
    decreased.population shouldBe 50
  }

  it should "not allow population to go below 0" in {
    val node = Node.withPopulation(10).build()
    val decreased = node.decreasePopulation(20)
    decreased.population shouldBe 0
  }

  "Builder validations" should "throw if infected > population" in {
    an[IllegalArgumentException] should be thrownBy {
      Node.withPopulation(50).withInfected(60).build()
    }
  }

  it should "throw if population is negative" in {
    an[IllegalArgumentException] should be thrownBy {
      Node.withPopulation(-1)
    }
  }

  it should "throw if infected is negative" in {
    an[IllegalArgumentException] should be thrownBy {
      Node.withPopulation(10).withInfected(-5)
    }
  }

  it should "throw if cureEffectiveness is out of bounds" in {
    an[IllegalArgumentException] should be thrownBy {
      Node.withPopulation(10).withCureEffectiveness(1.5)
    }
  }
