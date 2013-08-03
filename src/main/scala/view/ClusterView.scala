package view

import _root_.model.DefinitionNode
import _root_.model.DefinitionNode.Type.{ColumnFamily, Keyspace}
import swing._
import model._
import javax.swing._
import scala.swing.event.{Key, ButtonClicked, MouseClicked}
import util.{IconHelper, Logging}
import scalaswingcontrib.event.TreePathSelected
import moreswing.swing.{InternalFrame, DesktopPane, GridDesktopPane}
import scalaswingcontrib.PopupMenu
import scala.swing
import scala.swing
import scalaswingcontrib.tree.{TreeModel, Tree}
import java.awt.Toolkit
import com.apple.eawt.Application
import components.ExtendedDekstopPane
import javax.imageio.ImageIO
import javax.swing.tree.DefaultTreeCellRenderer

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
class ClusterView(cassandraUri: String) extends MainFrame with Logging {

  title = cassandraUri

  override def closeOperation() {
    dispose()
  }

  val newConnectionMenuItem = new MenuItem("New Connection")
  val refreshMenuItem = new MenuItem("Refresh")

  menuBar = new MenuBar {
    contents ++= Seq(
      new Menu("Connection") {
        contents += newConnectionMenuItem
      },
      new Menu("View") {
        contents += refreshMenuItem
      }
    )
  }

  val keyspaceIcon = IconHelper.fromResource("/icons/16x16/database.png")
  val columnFamilyIcon = IconHelper.fromResource("/icons/16x16/category-2.png")

  val treeRenderer = Tree.Renderer.labelled[DefinitionNode](node => {
    node.nodeType match {
      case Keyspace => (keyspaceIcon, node.name)
      case ColumnFamily =>  (columnFamilyIcon, node.name)
    }
  })

  val tree = new Tree[DefinitionNode] {
    model = TreeModel()(_.children)
    renderer = treeRenderer
    border = BorderFactory.createEmptyBorder()
    selection
  }

  val desktop = new ExtendedDekstopPane {
    backgroundImage = ImageIO.read(getClass.getResource("/desktop-background.jpg"))
  }

  object Popup {
    val browse = new MenuItem("Browse")
  }

  val popup = new PopupMenu {
    contents += Popup.browse
  }

  /*
          val node = keyspaceView.tree.selection.selectedNode
        val queryView = new QueryView(node.value)
        queryView.bounds = new Rectangle(10, 10, 600, 600)
        desktop += queryView
        queryView.visible = (true)
   */

  /*
  val tree = new Tree[DefinitionNode] {
    model = TreeModel()(_.children)
    renderer = Tree.Renderer(_.value)
  selection
  }

  add(new ScrollPane {
     contents = tree
  }, BorderPanel.Position.Center)
   */

//  val frame = new QueryView
//  desktop += frame
//  desktop.insert(0, frame)
//  frame.visible = true
//  frame.front

  preferredSize = new Dimension(1200, 900)
  contents = new BorderPanel {
    border = BorderFactory.createEmptyBorder()
    add(new SplitPane(Orientation.Vertical) {
      dividerSize = 2
      dividerLocation = 290
      leftComponent = new BorderPanel {
        add(new ScrollPane(tree) {
          border = BorderFactory.createEmptyBorder()
          horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
        }, BorderPanel.Position.Center)
      }
      rightComponent = desktop
    }, BorderPanel.Position.Center)
  }

  //Application.getApplication.setDefaultMenuBar(system.peer)
  centerOnScreen()
  visible = true
}
