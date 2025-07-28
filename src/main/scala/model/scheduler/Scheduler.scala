package model.scheduler

/**
 * Defines the contract for a scheduler that manages the timing of simulation ticks.
 */
sealed trait Scheduler:
  /**
   * The time interval in milliseconds between each tick.
   */
  val interval: Long

  /**
   * Pauses the current thread to wait for the next scheduled tick.
   * The interval determines the duration of the pause.
   */
  def waitForNextTick(): Unit

/**
 * A default scheduler that uses a fixed standard interval of 1000 ms.
 */
object FixedStandardRateScheduler extends Scheduler:

  override val interval = 1000L

  override def waitForNextTick(): Unit =
    Thread.sleep(interval)

/**
 * A scheduler that allows for a custom time interval.
 */
final case class CustomScheduler(override val interval: Long) extends Scheduler:
  require(interval > 0, "Interval must be greater than 0")

  override def waitForNextTick(): Unit =
    Thread.sleep(interval)
