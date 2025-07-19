package model.time
import TimeTypes.*

case class BasicYear(day: Day, year: Year) extends Time:
  override def +(days: Int): Time =
    require(days > 0, "Days must be non-negative")
    if day.value + days < 365 then BasicYear(Day(day.value + days), year)
    else
      val newDay = 365 - (day.value + days)
      BasicYear(Day(newDay), Year(year.value + 1))

  override def <(t: Time): Boolean =
    if year.value == t.year.value then day.value < t.day.value
    else year.value < t.year.value

  override def >(t: Time): Boolean =
    if year.value == t.year.value then day.value > t.day.value
    else year.value > t.year.value
