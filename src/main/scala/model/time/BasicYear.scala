package model.time
import TimeTypes.*

/**
 * A simple calendar year of 365 days.
 */
case class BasicYear(day: Day, year: Year) extends Time:
  private val DAYS_IN_YEAR = 365
  
  override def +(days: Int): Time =
    require(days > 0, "Days must be positive")
    val totalDays = day.value + days
    val newYearValue = year.value + totalDays / DAYS_IN_YEAR
    val newDayValue = totalDays % DAYS_IN_YEAR
    BasicYear(Day(newDayValue), Year(newYearValue))
  
  override def <(t: Time): Boolean =
    if year.value == t.year.value then day.value < t.day.value
    else year.value < t.year.value
  
  override def >(t: Time): Boolean =
    if year.value == t.year.value then day.value > t.day.value
    else year.value > t.year.value
