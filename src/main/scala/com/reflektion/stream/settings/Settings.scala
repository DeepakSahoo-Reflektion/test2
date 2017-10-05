package com.reflektion.stream.settings

import akka.actor.ActorSystem

class Settings(system:ActorSystem){

  object KafkaConsumers{
    val numberOfConsumers = system.settings.config.getInt("akka.kafka.consumer.num-consumers")

    //TODO: We only have one bootstrap server (kafka broker) at the moment so we get one IP below)
    val KafkaConsumerInfo: Map[String, Map[String,String]] = (for (i <- 1 to numberOfConsumers) yield {
      val kafkaMessageType = system.settings.config.getString(s"akka.kafka.consumer.c$i.message-type")
      val kafkaMessageBrokerIP = system.settings.config.getString(s"akka.kafka.consumer.c$i.bootstrap-servers")
      val kafkaTopic = system.settings.config.getString(s"akka.kafka.consumer.c$i.subscription-topic")
      val groupId = system.settings.config.getString(s"akka.kafka.consumer.c$i.groupId")
      kafkaMessageType -> Map("bootstrap-servers" -> kafkaMessageBrokerIP, "subscription-topic" -> kafkaTopic, "groupId" -> groupId)
    }).toMap
  }
}
object Settings {

  def apply(actorSystem: ActorSystem): Settings = new Settings(actorSystem)

}
