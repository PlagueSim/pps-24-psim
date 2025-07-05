package model.time
import TimeTypes.*

case class BasicYear(day: Day, year: Year) extends Time:
  override def +(days: Int): Time =
    require(days > 0, "Days must be non-negative")
    if day.value + days < 365 then BasicYear(Day(day.value + days), year)
    else
      val newDay = 365 - (day.value + days)
      BasicYear(Day(newDay), Year(year.value + 1))
