package test.producer1

import java.io.InputStream

import play.api.libs.json.{JsObject, JsString, Json}

object JsonTest extends App{

  val stream : InputStream = getClass.getResourceAsStream("/working3.json")
  val message = scala.io.Source.fromInputStream( stream ).mkString
  println(s"$message")
  val newJson = Json.parse(message)

  // val messageType = (msgJson \ "type").asOpt[String].getOrElse("")
  // val domain = (msgJson\"e_device"\"browser")
  // val browser = (msgJson \\ "browser")
  //println("Transforming the ExraDimensionData")
  //val newJson = tranformExtractedDimensionData(message)

  val newMeMsg = newJson.toString()

  println(s"$newMeMsg")
  val messageType = (newJson \ "type").asOpt[String].getOrElse("")
  val domain = (newJson \ "domain").asOpt[String].getOrElse("")
  //List(domain, id_type, uuid)
  val id_type = (newJson \ "id_type").asOpt[String].getOrElse("")

  val uuid = (newJson \ "uuid").asOpt[String].getOrElse("")

  val price = (newJson \ "details" \ "final_price").asOpt[String].getOrElse("")

  //Thread.sleep(10000)

  println(s"New message $messageType and domain type $domain and $price and $uuid and $id_type")




  def tranformExtractedDimensionData(message:String):JsObject = {
    val msgJson = Json.parse(message).as[JsObject]
    val messageTypeOp = (msgJson \ "type").asOpt[String]
    if(!messageTypeOp.isDefined){
      var newJson = msgJson
      (msgJson \ "e_event" \ "rfk_event_type").asOpt[String].foreach{ t=>
        newJson += ("type" -> JsString(t))
      }

      (msgJson \ "e_domain" \ "domain").asOpt[String].foreach{ t=>
        newJson += ("domain" -> JsString(t))
      }

      (msgJson \ "e_user" \ "uuid").asOpt[String].foreach{ t=>
        newJson += ("uuid" -> JsString(t))
      }

      (msgJson \ "e_product_info" \ "id").asOpt[String].foreach{ t=>
        newJson += ("product_id" -> JsString(t))
      }

      newJson
    }
    else{
      msgJson
    }
  }
}
