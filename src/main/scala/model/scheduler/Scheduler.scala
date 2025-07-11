package model.scheduler

sealed trait Scheduler:
  val interval: Long
  def waitForNextTick(): Unit

object FixedStandardRateScheduler extends Scheduler:

  override val interval = 1000L

  override def waitForNextTick(): Unit =
    Thread.sleep(interval)

final case class CustomScheduler(override val interval: Long) extends Scheduler:
  require(interval > 0, "Interval must be greater than 0")

  override def waitForNextTick(): Unit =
    Thread.sleep(interval)
