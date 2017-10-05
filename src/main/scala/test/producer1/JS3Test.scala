package test.producer1

import java.io.InputStream

import play.api.libs.json.{JsValue, _}

object JS3Test extends App{



  val stream : InputStream = getClass.getResourceAsStream("/ac.json")
  val message = scala.io.Source.fromInputStream( stream ).mkString
  val msgJson = tranformExtractedDimensionData(message)
  println(s"from msgJson $msgJson")

  //var msgJson = Json.parse(message).as[JsObject]
  val domainOp = (msgJson \ "domain")

  println(s"domain is $domainOp")
  //val (supportedDomain, messageType, domain, id) = auditAndDecide(msgJson)
  //println(supportedDomain)






  def auditAndDecide(msgJson: JsObject): (Boolean, String, String, String) = {
    //val msgJson = Json.parse(message).as[JsObject]

    val messageType = (msgJson \ "type").asOpt[String].getOrElse("")
    var supportedDomain = false
    var domain = (msgJson \ "domain").asOpt[String].getOrElse("")
    println(s"domain is $domain")
    var id = ""

    import collection.JavaConversions._
    //val supportedDomainsList = context.system.settings.config.getStringList("supported.domains").toList
    val supportedDomainsList=List("106723093")

    if ("product-feed".equalsIgnoreCase(messageType)) {
      id = (msgJson \ "product_id").asOpt[String].getOrElse("")
      domain = (msgJson \ "domain").asOpt[String].getOrElse("")

    } else if("analytics".equalsIgnoreCase(messageType) || "price-drop".equalsIgnoreCase(messageType)) {

      id = (msgJson \ "uuid").asOpt[String].getOrElse("")
      domain = (msgJson \ "domain").asOpt[String].getOrElse("")

    }
    else{
      println("inside else")
      val domainIntOp = (msgJson \ "domain").asOpt[Int]
      if(!domainIntOp.isDefined) {
        domain =""
      }else {
        domain = domainIntOp.get.toString
      }

      id = (msgJson \ "uuid").asOpt[String].getOrElse("")

    }


    if (supportedDomainsList.contains(domain)) supportedDomain = true
    println("ENTRY:Request with Type:{} and Domain:{} and Id:{}", messageType, domain, id)

    (supportedDomain, messageType, domain, id)
  }



  def tranformExtractedDimensionData(message:String):JsObject = {
    println("transformExtractedDimensionData")
    val msgJson = Json.parse(message).as[JsObject]
    val messageTypeOp = (msgJson \ "type").asOpt[String]
    var json = {
      var newJson = msgJson

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

    if("analytics".equalsIgnoreCase(messageTypeOp.getOrElse(""))){
      json += ("id_type" -> JsString("UUID"))
    }
    json
  }
}
