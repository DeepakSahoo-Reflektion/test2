package com.reflektion.stream.main

import akka.actor.{ActorSystem, Props}
import com.reflektion.stream.actors.ConsumerStreamManager
import com.reflektion.stream.actors.ConsumerStreamManager.InitializeConsumerStream
import com.reflektion.stream.settings.AkkaStream
import com.typesafe.config.ConfigFactory

object MainApp extends App with AkkaStream{
  override implicit val actorSystem: ActorSystem = ActorSystem("stream-system")

  val config=ConfigFactory.load()


  if(config.getBoolean("service.akkaStream")){
    println("Akka Stream is enabled")

    println("ConsumerStreamManager: Initialize ConsumerStreamManager")
    //initialize the consumerStreamManager
    val consumerStreamManager = actorSystem.actorOf(Props(new ConsumerStreamManager), "consumerStreamManager")


    consumerStreamManager ! InitializeConsumerStream
  }


}
