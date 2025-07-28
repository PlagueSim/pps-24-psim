package controller

import scalafx.application.Platform

import scala.concurrent.{ExecutionContext, Future}

/**
 * Defines different execution modes for the application, abstracting over how
 * UI updates and background tasks are handled. This allows the same application logic
 * to run in different environments, such as a GUI or a terminal.
 */
object ExecutionMode:

  /**
   * A sealed trait representing an execution mode. It provides a common interface
   * for running tasks on a specific thread (e.g., the UI thread) and executing
   * asynchronous operations.
   */
  sealed trait ExecutionMode:
    /**
     * Provides a global execution context for running futures.
     */
    given ExecutionContext = ExecutionContext.global
    /**
     * Schedules a `Runnable` to be executed at a later time, typically on a specific thread.
     */
    def runLater(r: Runnable): Unit
    /**
     * Executes a block of code asynchronously and returns a `Future` with the result.
     */
    def execute[T](body: => T): Future[T] =
      Future(body)

  /**
   * An execution mode for JavaFX GUI applications.
   */
  case object GuiFXMode extends ExecutionMode:
    def runLater(r: Runnable): Unit = Platform.runLater(r)

  /**
   * An execution mode for terminal-based applications.
   */
  case object TerminalMode extends ExecutionMode:
    def runLater(r: Runnable): Unit                = r.run()
    override def execute[T](body: => T): Future[T] =
      Future.successful(body)

  /**
   * A custom execution mode for GUI applications, allowing for a user-defined
   * `runLater` implementation.
   */
  case class CustomGuiExecutionMode(runLaterFunc: Runnable => Unit)
      extends ExecutionMode:
    def runLater(r: Runnable): Unit = runLaterFunc(r)
