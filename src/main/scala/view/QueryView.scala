package view

import _root_.model.{QueryMoreResultNode, QueryColumnResultNode, QueryRowResultNode, EmptyQueryResult}
import moreswing.swing.InternalFrame
import scala.swing._
import javax.swing._
import javax.swing.table.TableCellRenderer
import scala.collection.JavaConversions._

import java.awt._
import jsyntaxpane.DefaultSyntaxKit
import components.{FillPanel, ScrollablePanel, XTreeTable}
import scala.swing.Font
import scala.swing.Table.AutoResizeMode
import java.awt.Rectangle
import scala.swing.Label
import java.awt.Font
import scala.swing.Font
import scala.swing.ScrollPane
import scala.swing.Button
import java.awt.Dimension
import scala.swing.TextField
import org.jdesktop.swingx.decorator.HighlighterFactory
import javax.swing.tree.DefaultTreeCellRenderer
import util.IconHelper
import org.jdesktop.swingx.treetable.TreeTableNode
import org.jdesktop.swingx.renderer.{StringValue, DefaultTreeRenderer, IconValue}
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer

/**
 * author mikwie
 *
 */
class QueryView(title: String) extends InternalFrame(title, true, true, true, true) {

  val rowIcon = IconHelper.fromResource("/icons/16x16/administrative-docs.png")
  val columnIcon = IconHelper.fromResource("/icons/16x16/sign-in.png")

  val treeIconValue = new IconValue {
    def getIcon(value: Any): Icon = {
      value match {
        case v: QueryRowResultNode => rowIcon
        case v: QueryColumnResultNode => columnIcon
        case _ => IconValue.NULL_ICON
      }
    }
  }

  val treeStringValue = new StringValue {
    def getString(value: Any): String = {
      value match {
        case v: QueryRowResultNode => v.key
        case v: QueryColumnResultNode => v.name
        case v: QueryMoreResultNode => "(...)"
        case _ => "Nothing"
      }
    }
  }

  val treeTable = new XTreeTable(EmptyQueryResult) {
    highlighters = HighlighterFactory.createSimpleStriping(new Color(233, 237, 242))
    rowHeight = 30
    renderer = new DefaultTreeRenderer(treeIconValue, treeStringValue, false)
  }

  val rowKeyTextField = new TextField() {
    columns = 60
    editable = false
    border = BorderFactory.createEmptyBorder()
    background = null
  }

  object Query {
    val button = new Button("Search") {
      preferredSize = new Dimension(30, 20)
    }

    val field = new TextField("") {
      columns = 20
    }
  }

  val leftPanel = new FlowPanel(new GridPanel(0, 2) {
    contents ++= Seq(
      new Label("Row key:"),
      Query.field, new Label(""),
      Query.button
    )
  }) {
    border = BorderFactory.createTitledBorder("Query")
  }

  DefaultSyntaxKit.initKit()
  val editor = new components.EditorPane() {
    preferredSize = new Dimension(500, 200)
  }

  val treeTableScrollPane = new ScrollPane(treeTable) {
    preferredSize = new Dimension(500, 700)
  }

  val innerSplitPane = new BorderPanel {
    add(leftPanel, BorderPanel.Position.North)
    add(treeTableScrollPane, BorderPanel.Position.Center)
  }

  val outerSplitPane = new SplitPane(Orientation.Horizontal, innerSplitPane,
    new BorderPanel {
      add(new FlowPanel(FlowPanel.Alignment.Left)(new Label("Row:"), rowKeyTextField), BorderPanel.Position.North)
      add(new ScrollPane(editor), BorderPanel.Position.Center)
    }
  ) {
    dividerSize = 2
    dividerLocation = 500
  }

  val progressBar = new ProgressBar {
    preferredSize = new Dimension(200, 20)
  }

  contents = new BorderPanel {
    add(outerSplitPane, BorderPanel.Position.Center)
    add(new FlowPanel(FlowPanel.Alignment.Left)(progressBar), BorderPanel.Position.South)
  }

  editor.contentType = "text/javascript"
  editor.text = ""

  bounds = new Rectangle(10, 10, 800, 800)

  visible = true


}
