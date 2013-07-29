package view

import moreswing.swing.InternalFrame
import scala.swing.{ScrollPane, Table, BorderPanel}
import java.awt.{Point, Dimension, Rectangle}
import javax.swing.JScrollPane

/**
 * author mikwie
 *
 */
class QueryView(title: String) extends InternalFrame(title, true, true, true, true) {

  val table = new Table(Array(Array[Any]("1", "2", "3")), Seq("A", "B"))

  val leftPanel = new BorderPanel

  contents = new BorderPanel {
    add(leftPanel, BorderPanel.Position.West)
    add(new ScrollPane(table), BorderPanel.Position.Center)
  }

  preferredSize = new Dimension(400, 400)
  location = new Point(100, 100)



}
