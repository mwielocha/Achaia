package view

import _root_.model.DefinitionNode
import swing._
import model._
import javax.swing._
import scala.swing.event.{Key, ButtonClicked, MouseClicked}
import util.Logging
import scalaswingcontrib.event.TreePathSelected
import moreswing.swing.{InternalFrame, DesktopPane, GridDesktopPane}
import scalaswingcontrib.PopupMenu
import scala.swing
import scala.swing
import scalaswingcontrib.tree.{TreeModel, Tree}
import java.awt.Toolkit
import com.apple.eawt.Application

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
class ClusterView(title: String) extends MainFrame with Logging {

  val newConnectionMenuItem = new MenuItem("New Connection")

  menuBar = new MenuBar {
    contents += new Menu("Achaia") {
      contents += newConnectionMenuItem
    }
  }

  val tree = new Tree[DefinitionNode] {
    model = TreeModel()(_.children)
    renderer = Tree.Renderer(_.name)
    border = BorderFactory.createEmptyBorder()
    selection
  }

  val desktop = new GridDesktopPane

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
