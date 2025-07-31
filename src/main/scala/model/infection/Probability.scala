package model.infection

/** Provides a domain-specific type for representing probabilities.
  */
object Probability:
  /** An opaque type representing a probability value.
    */
  opaque type Probability = Double

  /** Companion object for the `Probability` type, providing factory methods and
    * extensions.
    */
  object Probability:
    private val MAX_PERCENTAGE = 100.0
    private val MIN_VALUE      = 0.0
    private val MAX_VALUE      = 1.0

    /** Creates a `Probability` instance from a percentage value. The given
      * percentage is converted to a value between 0.0 and 1.0. Values outside
      * the range [0, 100] are clamped to be within the valid range [0, 1].
      */
    def fromPercentage(p: Double): Probability =
      (p / MAX_PERCENTAGE).max(MIN_VALUE).min(MAX_VALUE)

    extension (p: Probability)
      /** Retrieves the underlying Double value of a `Probability` instance.
        */
      def value: Double = p
