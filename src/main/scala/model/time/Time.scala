package model.time

trait Time:
  import TimeTypes.*
  val day: Day
  val year: Year

  def season: Seasons = day.toSeason

  def +(days: Int): Time
  def <(t: Time): Boolean
  def >(t: Time): Boolean