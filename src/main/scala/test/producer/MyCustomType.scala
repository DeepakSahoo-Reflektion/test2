package test.producer

import java.io.UnsupportedEncodingException

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer


case class MyCustomType(a: Int,name:String)

class TweetsSerializer extends Serializer[Seq[MyCustomType]] {

  private var encoding = "UTF8"

  override def configure(configs: java.util.Map[String, _], isKey: Boolean):   Unit = {
    val propertyName = if (isKey) "key.serializer.encoding"
    else "value.serializer.encoding"
    var encodingValue = configs.get(propertyName)
    if (encodingValue == null) encodingValue = configs.get("serializer.encoding")
    if (encodingValue != null && encodingValue.isInstanceOf[String]) encoding = encodingValue.asInstanceOf[String]
  }

  override def serialize(topic: String, data: Seq[MyCustomType]): Array[Byte] =
    try
        if (data == null) return null
        else return {
          data.map { v =>
            println(s"${v.a.toString}")
            v.a.toString+""+v.name
          }
            .mkString("").getBytes("UTF8")
        }
    catch {
      case e: UnsupportedEncodingException =>
        throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + encoding)
    }

  override def close(): Unit = Unit

}


