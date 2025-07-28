package model.time

/**
 * Represents a generic concept of time in the simulation.
 */
trait Time:
  import TimeTypes.*
  /**
   * The day of the year.
   */
  val day: Day
  /**
   * The year.
   */
  val year: Year

  /**
   * Determines the current season based on the day of the year.
   */
  def season: Seasons = day.toSeason

  /**
   * Adds a specified number of days to the current time.
   */
  def +(days: Int): Time
  /**
   * Compares this `Time` instance with another to check if it is earlier.
   */
  def <(t: Time): Boolean
  /**
   * Compares this `Time` instance with another to check if it is later.
   */
  def >(t: Time): Boolean