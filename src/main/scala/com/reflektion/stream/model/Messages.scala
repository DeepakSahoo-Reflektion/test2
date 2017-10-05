package com.reflektion.stream.model



object EventMessages {

import akka.stream.scaladsl.SourceQueueWithComplete

abstract class EventMessage
//case class ActivatedConsumerStream(kafkaTopic: String) extends EventMessage
//case class TerminatedConsumerStream(kafkaTopic: String) extends EventMessage
//case class ActivatedProducerStream[msgType](producerStream: SourceQueueWithComplete[msgType], kafkaTopic: String) extends EventMessage
//case class MessagesPublished(numberOfMessages: Int) extends EventMessage
case class FailedMessageConversion(kafkaTopic: String, msg: String, msgType: String) extends EventMessage
}

object KafkaMessages{
  case class KafkaMessage(id:Int,name:String,age:Int)
}