package controller

import scalafx.application.Platform

import scala.concurrent.{ExecutionContext, Future}

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
