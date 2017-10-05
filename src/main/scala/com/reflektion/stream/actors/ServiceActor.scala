package com.reflektion.stream.actors

import akka.actor.Actor

object ServiceActor{


  sealed trait Response
  case class Success(msg:String) extends Response
  case class Failure(msg:String) extends  Response

}
class ServiceActor extends Actor {

  println("ServiceActor Constructor ")
  override def receive: Receive = {

    case msg =>
      println(s"Received the message $msg")
      val s = sender()
      s ! msg
  }
}
