package test.producer1

import java.io.InputStream

object ProducerExample extends App {

  import java.util.Properties

  import org.apache.kafka.clients.producer._

  //var loader=
  //val jsonData = scala.io.Source.fromResource("testfile.json")

  //working2.json is working

  val stream : InputStream = getClass.getResourceAsStream("/working2.json")
  val jsonData = scala.io.Source.fromInputStream( stream ).mkString

  println(s"jsonData $jsonData")
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC="test"

 /* for(i<- 1 to 50){
    val record = new ProducerRecord(TOPIC, "key", s"hello $i")
    producer.send(record)
  }*/

  val record = new ProducerRecord(TOPIC, "key", jsonData)
  for (i<-0 to 50)
  producer.send(record)

  producer.close()
}