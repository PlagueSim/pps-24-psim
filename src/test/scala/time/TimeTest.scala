package time

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.time.*
import model.time.TimeTypes.*

class TimeTest extends AnyFlatSpec with Matchers:

  "A day" should "be created with a valid value" in:
    Day(364).value shouldEqual 364

  it should "fail and throw an exception when created with an invalid value" in:
    an[IllegalArgumentException] should be thrownBy Day(-1)
    an[IllegalArgumentException] should be thrownBy Day(365)

  "A year" should "be created with a valid value" in:
    Year(2023).value shouldEqual 2023

  it should "fail and throw an exception when created with a negative value" in:
    an[IllegalArgumentException] should be thrownBy Year(-1)

  "With a day it" should "be able to determine the season of the year" in:
    Day(0).toSeason shouldEqual Seasons.Winter
    Day(100).toSeason shouldEqual Seasons.Spring
    Day(200).toSeason shouldEqual Seasons.Summer
    Day(300).toSeason shouldEqual Seasons.Autumn

  "A BasicYear" should "be created with a day and a year" in:
    val x = BasicYear(Day(0), Year(1))
    (x.day.value, x.year.value) shouldEqual (0, 1)

  it should "increment the day" in:
    val x = BasicYear(Day(0), Year(1))
    val y = x + 1
    (y.day.value, y.year.value) shouldEqual (1, 1)

  it should "handle itself the end of the year correctly" in:
    val x = BasicYear(Day(364), Year(1))
    val y = x + 1
    (y.day.value, y.year.value) shouldEqual (0, 2)
