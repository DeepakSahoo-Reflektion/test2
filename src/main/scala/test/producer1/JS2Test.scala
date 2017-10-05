package test.producer1

import java.io.InputStream

import play.api.libs.json.{JsObject, Json}

object JS2Test extends App{

  val stream : InputStream = getClass.getResourceAsStream("/working3.json")
  val message = scala.io.Source.fromInputStream( stream ).mkString
  println(s"$message")
  //val newJson = Json.parse(message)
  println(auditAndDecide(message))


  def auditAndDecide(message: String): (Boolean, String, String, String) = {
    val msgJson = Json.parse(message).as[JsObject]
    val messageType = (msgJson \ "type").asOpt[String].getOrElse("")
    var supportedDomain = false

    var id = ""
    var domain =""

   // println(s"$domain")


    //val supportedTypes = context.system.settings.config.ge

    import collection.JavaConversions._
    //val supportedTypesList = context.system.settings.config.getStringList("supported.types").toList
    //val supportedDomainsList = context.system.settings.config.getStringList("supported.domains").toList
    val supportedDomainsList=List("142422688")

    if ("product-feed".equalsIgnoreCase(messageType)) {
      id = (msgJson \ "product_id").asOpt[String].getOrElse("")
      var domain = (msgJson \ "domain").asOpt[String].getOrElse("")

    } else if("analytics".equalsIgnoreCase(messageType)) {

      id = (msgJson \ "uuid").asOpt[String].getOrElse("")
      var domain = (msgJson \ "domain").asOpt[String].getOrElse("")

    }else{

      val domainIntOp = (msgJson \ "domain").asOpt[Int]
      if(!domainIntOp.isDefined) {
        domain =""
      }else {
        domain = domainIntOp.get.toString
      }

      id = (msgJson \ "uuid").asOpt[String].getOrElse("")

    }
    println("ENTRY:Request with Type:{} and Domain:{} and Id:{}", messageType, domain, id)

    if (supportedDomainsList.contains(domain)) supportedDomain = true


    (supportedDomain, messageType, domain, id)
  }

}
