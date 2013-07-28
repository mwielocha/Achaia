package view

import _root_.model.impl.AstyanaxCluster
import scala.swing._
import javax.swing.{JScrollPane, JTextArea, JOptionPane, JLabel}
import java.io.{PrintWriter, StringWriter}
import util.ExceptionHandling

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
class ConnectionView(owner: Window) extends Dialog(owner) with ExceptionHandling {

  val nameTf = new TextField("Cluster")
  val hostTf = new TextField("localhost")
  val portTf = new TextField("9160")

  modal = true
  title = "New Connection"
  contents = new BorderPanel {
    add(new GridPanel(3, 2) {
      contents ++= Seq(
        new Label("name:"),
        nameTf,
        new Label("host:"),
        hostTf,
        new Label("port:"),
        portTf
      )
    }, BorderPanel.Position.Center)
    add(new Button(Action("Connect") {
      openClusterView
    }), BorderPanel.Position.South)
  }

  private def openClusterView {
    withExceptionHandling {
      new ClusterView(
        new AstyanaxCluster(nameTf.text, hostTf.text, portTf.text.toInt)).visible = true
      this.dispose()
    }
  }
}
