package com.reflektion.stream.actors

import akka.actor.{Actor, Props}
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Flow, Sink}
import com.reflektion.stream.model.EventMessages.FailedMessageConversion
import com.reflektion.stream.model.KafkaMessages.KafkaMessage
import com.reflektion.stream.settings.{AkkaStream, Settings}
import com.reflektion.stream.util.JsonMessageConversion.Conversion
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object ConsumerStreamManager{

  case object InitializeConsumerStream

  def props:Props = ConsumerStreamManager.props

}

/**
  * The purpose of the ConsumerStreamManager class is to manage the different ConsumerStreams
  * It can either start or stop different streams.
  */
class ConsumerStreamManager extends Actor with AkkaStream{

  import ConsumerStreamManager._
  implicit val actorSystem = context.system


  //the use of the Settings is to import the KafkaConsumer Settings into this scope. So that it can be used
  //to

  val settings = Settings(actorSystem).KafkaConsumers

  override def receive: Receive ={
    case InitializeConsumerStream =>
      println("ConsumerStreamManager:InitializeConsumerStream")

      //lets first get the kafkaconsumerconfig from the settings object
      val consumerConfig = settings.KafkaConsumerInfo
      println(s"KafkaConsumerConfig recived $consumerConfig")

      //To initialize a consumer stream we need Source,Flow and Sink.
      //and that information we are going to get from the Settings.KafkaConsumer class
      //we are getting KafkaConsumer config as a Map here
      startStreamConsumer(consumerConfig("KafkaMessage"))
      //process(consumerConfig("KafkaMessage"))


  }

  def process(config:Map[String,String]): Unit ={
    implicit val timeout = Timeout(2 seconds)
    val consumerSource = createConsumerSource(config)
    println("ConsumerSource created successfully")

    //create the consumer sink
    val consumerSink = createConsumerSink()

    val serviceActor = actorSystem.actorOf(Props[ServiceActor],"service-actor")

    /*consumerSource.map{ ip =>
      serviceActor ? ip
    }.to(consumerSink).run()*/

    consumerSource.map{ ip =>
      serviceActor ? ip
    }.runWith(consumerSink).onComplete{
      case _ => println("completed processing stoppig the service actor ")
        actorSystem.stop(serviceActor)
    }
  }

  def startStreamConsumer(config:Map[String,String])={
    println(s"StartStreamConsumer with config $config")

    //lets start the source,flow and sink individually and then we will hook them together
    //the source
    val consumerSource = createConsumerSource(config)
    println("ConsumerSource created successfully")

    //create the consumer sink
    val consumerSink = createConsumerSink()

    //create the consumer flow
    val consumerFlow = createConsumerFlow[KafkaMessage]

    //wire the source,flow and sink together to form the graphdsl and run
    println("Running the Stream")
    //consumerSource.map(println).to(consumerSink).run()
    /*consumerSource.map{ res =>
      println(s"value is ${res.record.value()}")

    }.to(consumerSink).run()*/
    println("After the stream run")
    consumerSource.via(consumerFlow).to(consumerSink).run()
    //consumerSource.to(consumerSink).run()

  }

  def createConsumerSource(config:Map[String,String])={
    val kafkaMBAddress = config("bootstrap-servers")
    val groupID = config("groupId")
    val topicSubscription = config("subscription-topic")
    val consumerSettings = ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaMBAddress)
      .withGroupId(groupID)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    Consumer.committableSource(consumerSettings, Subscriptions.topics(topicSubscription))
  }


    def createConsumerFlow[msgType: Conversion] = {
      Flow[ConsumerMessage.CommittableMessage[Array[Byte], String]]
        .map{msg =>
          println(s"message here $msg")
          (msg.committableOffset, Conversion[msgType].convertFromJson(msg.record.value))}
        //Publish the conversion error event messages returned from the JSONConversion
        .map (tuple => publishConversionErrors[msgType](tuple))
        .filter(result => result.isRight)
        .map(test => test.right.get)
        //Group the commit offsets and correctly converted messages for more efficient Kafka commits
        .batch(max = 20, tuple => (CommittableOffsetBatch.empty.updated(tuple._1), ArrayBuffer[msgType](tuple._2)))
      {(tupleOfCommitOffsetAndMsgs, tuple) =>
        (tupleOfCommitOffsetAndMsgs._1.updated(tuple._1), tupleOfCommitOffsetAndMsgs._2 :+ tuple._2)
      }
        //Take the first element of the tuple (set of commit numbers) to add to kafka commit log and then return the collection of grouped case class messages
        .mapAsync(4)(tupleOfCommitOffsetAndMsgs => commitOffsetsToKafka[msgType](tupleOfCommitOffsetAndMsgs))
        .map(msgGroup => msgGroup._2)
    }



  def publishConversionErrors[msgType](tupleOfCommitOffsetAndConversionResults: (ConsumerMessage.CommittableOffset, Either[FailedMessageConversion,msgType]))
  : Either[Unit,(ConsumerMessage.CommittableOffset,msgType)] = {

    if (tupleOfCommitOffsetAndConversionResults._2.isLeft) {

      //Publish a local event that there was a failure in conversion
      //publishLocalEvent(tupleOfCommitOffsetAndConversionResults._2.left.get)

      //Commit the Kafka Offset to acknowledge that the message was consumed
      Left(tupleOfCommitOffsetAndConversionResults._1.commitScaladsl())
    }
    else
      Right(tupleOfCommitOffsetAndConversionResults._1,tupleOfCommitOffsetAndConversionResults._2.right.get)
  }

  def commitOffsetsToKafka[msgType](tupleOfCommitOffsetAndMsgs: (ConsumerMessage.CommittableOffsetBatch, ArrayBuffer[msgType])) = Future {
    (tupleOfCommitOffsetAndMsgs._1.commitScaladsl(), tupleOfCommitOffsetAndMsgs._2)
  }

  def createConsumerSink()={
     //Sink.ignore
    Sink.foreach(println)
  }

}
