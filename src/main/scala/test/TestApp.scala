package test

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.ExecutionContext.Implicits.global
object TestApp extends App{

  implicit val system = ActorSystem("stream-system")
  implicit val materializer = ActorMaterializer()
  //implicit val ec = system.dispatcher

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("test-group-id")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer.committableSource(consumerSettings, Subscriptions.topics("test"))
    .map(x =>
      println(s"value $x")).runWith(Sink.ignore)







}
