package test.producer1

import java.io.InputStream

import play.api.libs.json.{JsObject, JsString, Json}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LeakTest extends App{

  val stream : InputStream = getClass.getResourceAsStream("/working3.json")
  val message = scala.io.Source.fromInputStream( stream ).mkString

  type UNIQUE_ID=(Boolean, String, String, String)
  while (true){
    Future{
      val (unique_id,transformedMsg) = enrichAndAuditMessage(message)
    }

  }

  def enrichAndAuditMessage(message:String):(UNIQUE_ID,String)={

    def tranformExtractedDimensionData(message:String):JsObject = {
      val msgJson = Json.parse(message).as[JsObject]
      val messageTypeOp = (msgJson \ "type").asOpt[String]

      var newJson = msgJson

      (msgJson \ "e_domain" \ "domain").asOpt[String].foreach{ t=>
        newJson += ("domain" -> JsString(t))
      }

      (msgJson \ "e_user" \ "uuid").asOpt[String].foreach{ t=>
        newJson += ("uuid" -> JsString(t))
      }

      (msgJson \ "e_product_info" \ "id").asOpt[Int].foreach{ t=>
        newJson += ("product_id" -> JsString(t.toString))
      }

      newJson

    }


    var msgJson = tranformExtractedDimensionData(message)

    val messageType = (msgJson \ "type").asOpt[String].getOrElse("")
    var domain=""
    var id=""
    var isSupportedDomain=false

    messageType match {
      case "analytics" =>
        id = (msgJson \ "uuid").asOpt[String].getOrElse("")
        msgJson += ("id_type" -> JsString("UUID"))
        domain = (msgJson \ "domain").asOpt[String].getOrElse("")
      case "product-feed" =>
        id = (msgJson \ "product_id").asOpt[String].getOrElse("")
        domain = (msgJson \ "domain").asOpt[String].getOrElse("")
      case "abandoned-cart-product" | "abandoned-browse-product" | "abandoned-browse-category" =>
        id = (msgJson \ "uuid").asOpt[String].getOrElse("")

        val domainIntOp = (msgJson \ "domain").asOpt[Int]
        if(domainIntOp.isDefined) domain=domainIntOp.get.toString
        msgJson += ("domain" -> JsString(domain))
      case _ =>
        id = (msgJson \ "uuid").asOpt[String].getOrElse("")
        domain = (msgJson \ "domain").asOpt[String].getOrElse("")
    }

    isSupportedDomain = true
    //log.info("ENTRY:Request | Type:{} | Domain:{} | Id:{}", messageType, domain, id)

    ((isSupportedDomain, messageType, domain, id),msgJson.toString())
  }
}
