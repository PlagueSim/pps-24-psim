package view.world

import scalafx.scene.text.Text
import javafx.scene.Node as FxNode
import model.world.Types.*
case class NodeView(
                     visuals: Seq[FxNode],
                     position: () => (PosX, PosY),
                     labels: Map[String, Text],
                     var population: Int = 0,
                     var infected: Int = 0,
                     var died: Int = 0 
                   ):

  /**
   * @param from The NodeView instance from which to copy label values.
   * This method updates the population, infected, and died counts
   */
  def updateLabels(from: NodeView): Unit =
    this.population = from.population
    this.infected = from.infected
    this.died = from.died
    this.labels.get("pop").foreach(_.text = from.labels("pop").text())
    this.labels.get("inf").foreach(_.text = from.labels("inf").text())
    this.labels.get("died").foreach(_.text = from.labels("died").text())

  /**
   * @param population The new population count to set.
   * This method updates the population count and the corresponding label.
   * */
  def updatePopulation(population: Int): Unit =
    this.population = population
    labels.get("pop").foreach(_.text = s"Pop: $population")

  /**
   * 
   * @param infected The new infected count to set.
   * This method updates the infected count and the corresponding label.
   */
  def updateInfected(infected: Int): Unit =
    this.infected = infected
    labels.get("inf").foreach(_.text = s"Infected: $infected")

  /**
   * 
   * @param died The new died count to set.
   * This method updates the died count and the corresponding label.
   */
  def updateDied(died: Int): Unit =
    this.died = died
    labels.get("died").foreach(_.text = s"Died: $died")


object NodeView:
  /**
   * Factory method to create a NodeView from individual label elements.
   *
   * @param id the ID of the node (used only for the label)
   * @param visuals the visual JavaFX nodes representing the NodeView
   * @param position a function returning the current position of the node
   * @param labelId the label showing the node ID
   * @param labelPop the label showing the population
   * @param labelInf the label showing the infected count
   * @param labelDied the label showing the death count
   * 
   * @return a new NodeView instance
   */
  def apply(
             id: NodeId,
             visuals: Seq[FxNode],
             position: () => (PosX, PosY),
             labelId: Text,
             labelPop: Text,
             labelInf: Text,
             labelDied: Text
           ): NodeView =
    NodeView(
      visuals = visuals,
      position = position,
      labels = Map(
        "id"   -> labelId,
        "pop"  -> labelPop,
        "inf"  -> labelInf,
        "died" -> labelDied
      )
    )
