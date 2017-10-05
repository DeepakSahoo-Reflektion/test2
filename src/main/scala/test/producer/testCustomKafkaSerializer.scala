package test.producer

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

object testCustomKafkaSerializer extends App {


  implicit val producerConfig = {
    val props = new Properties()
    props.setProperty("bootstrap.servers", "localhost:9092")
    props.setProperty("key.serializer", classOf[StringSerializer].getName)
    props.setProperty("value.serializer", classOf[TweetsSerializer].getName)
    props
  }

  lazy val kafkaProducer = new KafkaProducer[String, Seq[MyCustomType]](producerConfig)

  // Create scala future from Java
  private def publishToKafka(id: String, data: Seq[MyCustomType]) = {
    kafkaProducer
      .send(new ProducerRecord("test", id, data))
      .get()
  }

  val input = MyCustomType(1,"Deepak")

  publishToKafka("customSerializerTopic", Seq(input))


}