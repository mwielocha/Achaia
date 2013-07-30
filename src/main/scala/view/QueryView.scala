package view

import moreswing.swing.InternalFrame
import scala.swing._
import javax.swing._
import javax.swing.table.TableCellRenderer
import _root_.model.{ColumnModel, RowModel}
import scala.collection.JavaConversions._
import _root_.model.ColumnModel
import _root_.model.RowModel
import javax.swing.event.ListDataListener
import _root_.model.RowModel
import scala.swing
import java.awt.{FlowLayout, Dimension, Rectangle}
import jsyntaxpane.DefaultSyntaxKit

/**
 * author mikwie
 *
 */
class QueryView(title: String) extends InternalFrame(title, true, true, true, true) {

  val browseView = new BrowseView[String, AnyRef]

  object Query {
    val button = new Button("Search") {
      horizontalAlignment = Alignment.Left
    }

    val field = new TextField("") {
      horizontalAlignment = Alignment.Left
    }
  }

  val leftPanel = new FlowPanel(new GridPanel(3, 1) {
    border = BorderFactory.createEmptyBorder()
    contents ++= Seq[Component](
      new Label("Row key:") {
        horizontalAlignment = Alignment.Left
      }, Query.field, Query.button
    )
  })

  DefaultSyntaxKit.initKit()
  val editor = new util.EditorPane() {
    preferredSize = new Dimension(500, 400)
  }

  contents = new BorderPanel {
    add(leftPanel, BorderPanel.Position.West)
    add(new ScrollPane(browseView), BorderPanel.Position.Center)
    add(new ScrollPane(editor), BorderPanel.Position.South)
  }

  editor.contentType = "text/javascript"
  editor.text = ""

  bounds = new Rectangle(10, 10, 500, 500)

  visible = true


}
