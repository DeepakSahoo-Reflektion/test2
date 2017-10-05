package test.producer1

import java.io.InputStream
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import test.producer1.ProducerExample.getClass

object ProducerTest extends App{

  val jsonData="""
    | {"name":"jiiijij"}
    |
  """.stripMargin

  println(s"jsonData $jsonData")
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC="test"

   for(i<- 1 to 50){
     //val record = new ProducerRecord(TOPIC, "key", s"hello $i")
     val record = new ProducerRecord(TOPIC, "key", jsonData)
     producer.send(record)
   }

  //val record = new ProducerRecord(TOPIC, "key", jsonData)
  //producer.send(record)
  //producer.send(record)

  producer.close()
}
