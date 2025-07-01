package model.time

enum Seasons:
  case Spring, Summer, Autumn, Winter

object TimeTypes:

  private val SpringStart    = 80
  private val SummerStart    = 173
  private val AutumnStart    = 267
  private val SpringEnd      = SummerStart - 1
  private val SummerEnd      = AutumnStart - 1
  private val AutumnEnd      = 356
  private val StartYearValue = 0
  private val EndYearValue   = 365

  opaque type Day  = Int
  opaque type Year = Int

  object Day:
    def apply(value: Int): Day =
      require(
        value >= StartYearValue && value < EndYearValue,
        "Day must be between 0 and 364"
      )
      value
    extension (d: Day)
      def value: Int        = d
      def toSeason: Seasons = d match
        case d if d >= SpringStart && d <= SpringEnd => Seasons.Spring
        case d if d >= SummerStart && d <= SummerEnd => Seasons.Summer
        case d if d >= AutumnStart && d <= AutumnEnd => Seasons.Autumn
        case _                                       => Seasons.Winter

  object Year:
    def apply(value: Int): Year =
      require(value >= 0, "Year must be non-negative")
      value
    extension (y: Year) def value: Int = y
