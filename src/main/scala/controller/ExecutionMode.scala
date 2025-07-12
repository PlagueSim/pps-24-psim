package controller

import scalafx.application.Platform

import scala.concurrent.Future

sealed trait ExecutionMode:
  val runLater: Runnable => Unit
  def execute[T](body: => T): Either[Future[T], T]

case object GuiFXMode extends ExecutionMode:
  val runLater: Runnable => Unit                   = Platform.runLater
  def execute[T](body: => T): Either[Future[T], T] =
    Left(Future(body))

case object TerminalMode extends ExecutionMode:
  val runLater: Runnable => Unit                            = _.run()
  override def execute[T](body: => T): Either[Future[T], T] =
    Right(body)

case class CustomGuiExecutionMode(runLaterFunc: Runnable => Unit)
    extends ExecutionMode:
  val runLater: Runnable => Unit                            = runLaterFunc
  override def execute[T](body: => T): Either[Future[T], T] =
    Left(Future(body))
