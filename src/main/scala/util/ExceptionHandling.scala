package util

import view.ClusterView
import java.io.{PrintWriter, StringWriter}
import javax.swing.{JOptionPane, JScrollPane, JTextArea}
import java.awt.Dimension

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
trait ExceptionHandling {

  def withExceptionHandling(work: => Any) = {
    try {
      work
    } catch {
      case e: Exception => {
        val writer = new StringWriter()
        val printWriter = new PrintWriter(writer)
        e.printStackTrace(printWriter)
        val textArea = new JTextArea(writer.toString)
        textArea.setEnabled(false)
        val scrollPane = new JScrollPane(textArea) {
          override def getPreferredSize: Dimension = new Dimension(800, 500)
        }
        JOptionPane.showMessageDialog(null, scrollPane, "Error", JOptionPane.ERROR_MESSAGE)
      }
    }
  }
}
