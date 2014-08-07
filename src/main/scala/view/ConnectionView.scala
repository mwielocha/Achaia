package view

import scala.swing._
import javax.swing._
import java.io.{PrintWriter, StringWriter}
import util.ExceptionHandling
import controller.ClusterController
import components.ImagePanel
import javax.imageio.ImageIO
import javax.swing.border.{EtchedBorder, Border}

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
class ConnectionView extends Dialog with ExceptionHandling {

  import ComboBox.stringEditor

  val hostComboBox = new ComboBox[String](Nil)

  hostComboBox.makeEditable()

  val connect = new Button("Connect")

  val imagePanel = new ImagePanel(getClass.getResource("/cassandra.jpg")) {
    preferredSize = new Dimension(536, 393)
  }

  modal = false
  title = "New Connection"
  contents = new BorderPanel {
    add(imagePanel, BorderPanel.Position.North)
    add(hostComboBox, BorderPanel.Position.Center)
    add(connect, BorderPanel.Position.South)
  }

  size = new Dimension(536, 393 + 80)
  resizable = false
  centerOnScreen()
  visible = true
}
