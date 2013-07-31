package components

import scala.swing.Component
import org.jdesktop.swingx.JXImagePanel
import java.net.URL
import java.awt.Image

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 31.07.2013
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
class ImagePanel(val url: URL) extends Component {

  override lazy val peer = new JXImagePanel(url)

}
