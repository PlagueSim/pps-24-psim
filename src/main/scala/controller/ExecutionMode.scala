package controller

import scalafx.application.Platform

import scala.concurrent.{ExecutionContext, Future}
//
//sealed trait ExecutionMode:
//  val runLater: Runnable => Unit
//  def execute[T](body: => T): Either[Future[T], T]
//
//case object GuiFXMode extends ExecutionMode:
//  private given ExecutionContext                   = ExecutionContext.global
//  val runLater: Runnable => Unit                   = Platform.runLater
//  def execute[T](body: => T): Either[Future[T], T] =
//    Left(Future(body))
//
//case object TerminalMode extends ExecutionMode:
//  val runLater: Runnable => Unit                            = _.run()
//  override def execute[T](body: => T): Either[Future[T], T] =
//    Left(Future.successful(body))
//
//case class CustomGUIMode(runLaterFunc: Runnable => Unit)
//    extends ExecutionMode:
//  private given ExecutionContext = ExecutionContext.global
//  val runLater: Runnable => Unit = runLaterFunc
//  override def execute[T](body: => T): Either[Future[T], T] =
//    Left(Future(body))

object ExecutionMode:

  sealed trait ExecutionMode:
    given ExecutionContext = ExecutionContext.global
    def runLater(r: Runnable): Unit
    def execute[T](body: => T): Future[T] =
      Future(body)
  
  case object GuiFXMode extends ExecutionMode:
    def runLater(r: Runnable): Unit = Platform.runLater(r)
  
  case object TerminalMode extends ExecutionMode:
    def runLater(r: Runnable): Unit                = r.run()
    override def execute[T](body: => T): Future[T] =
      Future.successful(body)
  
  case class CustomGuiExecutionMode(runLaterFunc: Runnable => Unit)
      extends ExecutionMode:
    def runLater(r: Runnable): Unit = runLaterFunc(r)
