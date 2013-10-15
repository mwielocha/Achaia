package util

import view.ClusterView
import java.io.{PrintWriter, StringWriter}
import javax.swing.{UIManager, JOptionPane, JScrollPane, JTextArea}
import java.awt.{BorderLayout, Dimension}
import scala.swing._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
trait ExceptionHandling {

  class ErrorDialog(message: String, stackTrace: String) extends Dialog {
    title = "Error"
    modal = true
    contents = new BorderPanel {
      add(new FlowPanel(FlowPanel.Alignment.Center)(
        new Label("Error occured: " + message)), BorderPanel.Position.North)
      add(new ScrollPane(new TextArea(stackTrace) {
        editable = false
      }) {
        preferredSize = new Dimension(400, 220)
      }, BorderPanel.Position.Center)
      add(new FlowPanel(FlowPanel.Alignment.Right)(new Button(Action("Ok") {
        ErrorDialog.this.dispose()
        Unit
      })), BorderPanel.Position.South)
      add(new Label {
        icon = UIManager.getIcon("OptionPane.errorIcon")
        preferredSize = new Dimension(icon.getIconWidth + 10, icon.getIconHeight)
        verticalAlignment = Alignment.Top
      }, BorderPanel.Position.West)
    }
  }

  def withExceptionHandling(work: => Any) = {
    try {
      work
    } catch {
      case e: Exception => {
        val writer = new StringWriter()
        val printWriter = new PrintWriter(writer)
        e.printStackTrace(printWriter)
        val errorDialog = new ErrorDialog(e.getMessage, writer.toString)
        errorDialog.centerOnScreen()
        errorDialog.visible = true
      }
    }
  }
}
