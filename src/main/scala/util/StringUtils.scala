package util

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 15.10.2013
 * Time: 21:04
 * To change this template use File | Settings | File Templates.
 */
object StringUtils {

  def isNullOrEmpty(input: String): Boolean = {
    input == null || input.isEmpty
  }
}
