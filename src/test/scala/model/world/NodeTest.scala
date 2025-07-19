package model.world

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodeTest extends AnyFlatSpec with Matchers:

  import model.world.Node

  import model.world.Node.*
  
  
  "updateDied" should "update the node's infected and population correctly" in:
    val node = Node.withPopulation(100).withInfected(30).build()
    val updatedNode = node.updateDied(10)
    updatedNode.population shouldBe 90
    updatedNode.infected shouldBe 20
    updatedNode.died shouldBe 10

  it should "build a node with population and infected" in:
    val node = Node.withPopulation(100).withInfected(25).build()
    node.population shouldBe 100
    node.infected shouldBe 25
  

  "infectedPercentage" should "return 0.0 when population is 0" in:
    val node = Node.withPopulation(0).build()
    node.infectedPercentage() shouldBe 0.0

  it should "return correct value when population > 0" in:
    val node = Node.withPopulation(100).withInfected(25).build()
    node.infectedPercentage() shouldBe 0.25
  
  

  "applyInfection" should "increase infected without exceeding population" in:
    val node = Node.withPopulation(100).withInfected(60).build()
    val infected = node.applyInfection(50)
    infected.infected shouldBe 100

  "heal" should "reduce infected by given count" in:
    val node = Node.withPopulation(100).withInfected(30).build()
    val healed = node.heal(10)
    healed.infected shouldBe 20

  it should "not allow infected to go below 0" in:
    val node = Node.withPopulation(100).withInfected(5).build()
    val healed = node.heal(10)
    healed.infected shouldBe 0

  "increasePopulation" should "increase the total population" in:
    val node = Node.withPopulation(50).build()
    val increased = node.increasePopulation(20)
    increased.population shouldBe 70

  "decreasePopulation" should "reduce population correctly" in:
    val node = Node.withPopulation(80).build()
    val decreased = node.decreasePopulation(30)
    decreased.population shouldBe 50

  it should "not allow population to go below 0" in:
    val node = Node.withPopulation(10).build()
    val decreased = node.decreasePopulation(20)
    decreased.population shouldBe 0

  "Builder validations" should "throw if infected > population" in:
    an[IllegalArgumentException] should be thrownBy:
      Node.withPopulation(50).withInfected(60).build()

  it should "throw if population is negative" in:
    an[IllegalArgumentException] should be thrownBy:
      Node.withPopulation(-1)

  it should "throw if infected is negative" in:
    an[IllegalArgumentException] should be thrownBy:
      Node.withPopulation(10).withInfected(-5)

    
    

