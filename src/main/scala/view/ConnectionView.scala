package view

import scala.swing._
import javax.swing.{JScrollPane, JTextArea, JOptionPane, JLabel}
import java.io.{PrintWriter, StringWriter}
import util.ExceptionHandling
import controller.ClusterController

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
class ConnectionView extends Dialog with ExceptionHandling {

  val nameTextField = new TextField
  val hostTestField = new TextField
  val portTextField = new TextField

  val connect = new Button("Connect")

  modal = false
  title = "New Connection"
  contents = new BorderPanel {
    add(new GridPanel(3, 2) {
      contents ++= Seq(
        new Label("name:"),
        nameTextField,
        new Label("host:"),
        hostTestField,
        new Label("port:"),
        portTextField
      )
    }, BorderPanel.Position.Center)
    add(connect, BorderPanel.Position.South)
  }

  bounds = new Rectangle(400, 500, 400, 140)
  resizable = false
  visible = true
}
