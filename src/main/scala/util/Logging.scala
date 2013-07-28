package util

import org.apache.log4j.Logger


/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
trait Logging {

  val logger = Logger.getLogger(getClass)

}
