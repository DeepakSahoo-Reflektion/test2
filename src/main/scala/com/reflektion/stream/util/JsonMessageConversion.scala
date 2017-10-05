package com.reflektion.stream.util

import spray.json.DefaultJsonProtocol
import akka.util.Timeout
import com.reflektion.stream.model.EventMessages.FailedMessageConversion
import com.reflektion.stream.model.KafkaMessages.KafkaMessage
import play.api.libs.json.Json
import spray.json._

import scala.concurrent.duration._


object JsonMessageConversion extends DefaultJsonProtocol {

  implicit val resolveTimeout = Timeout(3 seconds)

  trait Conversion[T] {
    def convertFromJson(msg: String): Either[FailedMessageConversion, T]
  }

  //Here is where we create implicit objects for each Message Type you wish to convert to/from JSON
  object Conversion extends DefaultJsonProtocol {

    implicit object KafkaMessageConversions extends Conversion[KafkaMessage] {
      implicit val json3 = jsonFormat3(KafkaMessage)

      /**
        * Converts the JSON string from the CommittableMessage to KafkaMessage case class
        *
        * @param msg is the json string to be converted to KafkaMessage case class
        * @return either a KafkaMessage or Unit (if conversion fails)
        */
      def convertFromJson(msg: String): Either[FailedMessageConversion, KafkaMessage] = {
        try {
          println(s"let convert the message $msg")
          Right(msg.parseJson.convertTo[KafkaMessage])
        }
        catch {
          case e: Exception =>
            println("Failed to convert message")
            Left(FailedMessageConversion("kafkaTopic", msg, "to: KafkaMessage"))
        }
      }


    }


    //Adding some sweet sweet syntactic sugar
    def apply[T: Conversion]: Conversion[T] = implicitly
  }

}
