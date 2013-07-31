package util

import com.google.gson.{JsonParseException, JsonElement, JsonParser, GsonBuilder}

/**
 * author mikwie
 *
 */
object JsonFormatter {

  lazy val gson = new GsonBuilder().setPrettyPrinting().create()
  lazy val parser = new JsonParser

  def validate(input: String): Option[JsonElement] = {
    try {
      Some(parser.parse(input))
    } catch {
      case _ => {
        None
      }
    }
  }

  def format(input: String): String = {
    validate(input) match {
      case Some(json) => gson.toJson(json)
      case None => input
    }
  }
}
