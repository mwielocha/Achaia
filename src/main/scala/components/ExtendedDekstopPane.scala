package components

import moreswing.swing.DesktopPane
import javax.swing.JDesktopPane
import java.awt.{Graphics2D, Image, Graphics}

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 03.08.2013
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
class ExtendedDekstopPane extends DesktopPane {

  override lazy val peer = new JDesktopPane {

    override def paintComponent(g: Graphics) {
      super.paintComponent(g)


      if(backgroundImage != null) {
        val graphics2D = g.asInstanceOf[Graphics2D]
        graphics2D.drawImage(backgroundImage, 0, 0, this)
      }
    }
  }

  var bgImage: Image = null

  def backgroundImage = bgImage

  def backgroundImage_=(image: Image) = {
    bgImage = image
  }
}
