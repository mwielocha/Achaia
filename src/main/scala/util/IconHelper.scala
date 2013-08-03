package util

import javax.swing.ImageIcon

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 03.08.2013
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
object IconHelper {

  def fromResource(path: String) = {
    val url: java.net.URL = getClass.getResource(path)
    new ImageIcon(url)
  }
}
