package model.time

/**
 * Defines the seasons of the year.
 */
enum Seasons:
  case Spring, Summer, Autumn, Winter

/**
 * Provides opaque types for time-related values like Day and Year,
 * ensuring domain-specific constraints are met.
 */
object TimeTypes:

  private val SpringStart    = 80
  private val SummerStart    = 173
  private val AutumnStart    = 267
  private val SpringEnd      = SummerStart - 1
  private val SummerEnd      = AutumnStart - 1
  private val AutumnEnd      = 356
  private val StartYearValue = 0
  private val EndYearValue   = 365

  /**
   * An opaque type representing the day of the year.
   * It is constrained to be an integer between 0 and 364.
   */
  opaque type Day  = Int
  /**
   * An opaque type representing the year.
   */
  opaque type Year = Int

  /**
   * Companion object for the `Day` type.
   */
  object Day:
    /**
     * Creates a `Day` instance, validating that the value is within the allowed range.
     */
    def apply(value: Int): Day =
      require(
        value >= StartYearValue && value < EndYearValue,
        "Day must be between 0 and 364"
      )
      value
    extension (d: Day)
      /**
       * Retrieves the integer value of the `Day`.
       */
      def value: Int        = d
      /**
       * Converts the day of the year to the corresponding season.
       */
      def toSeason: Seasons = d match
        case d if d >= SpringStart && d <= SpringEnd => Seasons.Spring
        case d if d >= SummerStart && d <= SummerEnd => Seasons.Summer
        case d if d >= AutumnStart && d <= AutumnEnd => Seasons.Autumn
        case _                                       => Seasons.Winter

  /**
   * Companion object for the `Year` type.
   */
  object Year:
    /**
     * Creates a `Year` instance, validating that the value is non-negative.
     */
    def apply(value: Int): Year =
      require(value >= 0, "Year must be non-negative")
      value
    extension (y: Year)
      /**
       * Retrieves the integer value of the `Year`.
       */
      def value: Int = y
