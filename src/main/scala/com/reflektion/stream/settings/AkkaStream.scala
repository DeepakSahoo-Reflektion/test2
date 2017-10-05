package com.reflektion.stream.settings

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer

trait AkkaStream {

  implicit val actorSystem:ActorSystem
  implicit def executionContext = actorSystem.dispatcher
  implicit def materializer = ActorMaterializer()
}
