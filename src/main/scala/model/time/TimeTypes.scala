package model.time

enum Seasons:
  case Spring, Summer, Autumn, Winter

object TimeTypes:

  opaque type Day  = Int
  opaque type Year = Int

  object Day:
    def apply(value: Int): Day =
      require(value >= 0 && value < 365, "Day must be between 0 and 364")
      value
    extension (d: Day)
      def value: Int        = d
      def toSeason: Seasons = d match
        case d if d >= 80 && d <= 172  => Seasons.Spring
        case d if d >= 173 && d <= 266 => Seasons.Summer
        case d if d >= 267 && d <= 356 => Seasons.Autumn
        case _                         => Seasons.Winter

  object Year:
    def apply(value: Int): Year =
      require(value >= 0, "Year must be non-negative")
      value
    extension (y: Year) def value: Int = y
