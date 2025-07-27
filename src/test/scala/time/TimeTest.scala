package time

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.time.*
import model.time.TimeTypes.*

class TimeTest extends AnyFlatSpec with Matchers:

  private val VALID_DAY = 364
  private val INVALID_DAY_NEG = -1
  private val INVALID_DAY_OVER = 365
  private val VALID_YEAR = 2023
  private val INVALID_YEAR = -1
  private val WINTER_DAY = 0
  private val SPRING_DAY = 100
  private val SUMMER_DAY = 200
  private val AUTUMN_DAY = 300
  private val BASIC_YEAR_DAY = 0
  private val BASIC_YEAR_YEAR = 1
  private val END_OF_YEAR_DAY = 364
  private val NEXT_DAY = 1
  private val NEXT_YEAR = 2

  "A day" should "be created with a valid value" in:
    Day(VALID_DAY).value shouldEqual VALID_DAY

  it should "fail and throw an exception when created with an invalid value" in:
    an[IllegalArgumentException] should be thrownBy Day(INVALID_DAY_NEG)
    an[IllegalArgumentException] should be thrownBy Day(INVALID_DAY_OVER)

  "A year" should "be created with a valid value" in:
    Year(VALID_YEAR).value shouldEqual VALID_YEAR

  it should "fail and throw an exception when created with a negative value" in:
    an[IllegalArgumentException] should be thrownBy Year(INVALID_YEAR)

  "With a day it" should "be able to determine the season of the year" in:
    Day(WINTER_DAY).toSeason shouldEqual Seasons.Winter
    Day(SPRING_DAY).toSeason shouldEqual Seasons.Spring
    Day(SUMMER_DAY).toSeason shouldEqual Seasons.Summer
    Day(AUTUMN_DAY).toSeason shouldEqual Seasons.Autumn

  "A BasicYear" should "be created with a day and a year" in:
    val x = BasicYear(Day(BASIC_YEAR_DAY), Year(BASIC_YEAR_YEAR))
    (x.day.value, x.year.value) shouldEqual (BASIC_YEAR_DAY, BASIC_YEAR_YEAR)

  it should "increment the day" in:
    val x = BasicYear(Day(BASIC_YEAR_DAY), Year(BASIC_YEAR_YEAR))
    val y = x + NEXT_DAY
    (y.day.value, y.year.value) shouldEqual (NEXT_DAY, BASIC_YEAR_YEAR)

  it should "handle itself the end of the year correctly" in:
    val x = BasicYear(Day(END_OF_YEAR_DAY), Year(BASIC_YEAR_YEAR))
    val y = x + NEXT_DAY
    (y.day.value, y.year.value) shouldEqual (BASIC_YEAR_DAY, NEXT_YEAR)
