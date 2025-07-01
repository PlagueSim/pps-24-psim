package model.time

trait Time:
  import TimeTypes.*
  def day: Day
  def year: Year

  def season: Seasons = day.toSeason

  def +(days: Int): Time
