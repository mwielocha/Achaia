package controller

import view.ClusterView
import cassandra.CassandraAware
import model.DefinitionNode
import scalaswingcontrib.tree.TreeModel
import scala.swing.event.{ButtonClicked, Key, MouseClicked}
import Key.Modifier._

/**
 * author mikwie
 *
 */
class ClusterController {

  self: CassandraAware =>

  val view = new ClusterView(cassandraService.cassandraUri)

  var nodeModel: (DefinitionNode, DefinitionNode) = _

  def refresh = {
    val treeModel: Seq[DefinitionNode] = cassandraService.getKeyspaces.sorted.map(k => {
      import DefinitionNode.Type._
      DefinitionNode(k, Keyspace, cassandraService.getColumnFamilies(k).sorted.map(DefinitionNode(_, ColumnFamily)))
    })

    view.tree.model = TreeModel[DefinitionNode](treeModel: _*)(_.children)
    view.tree.expandAll
    view.tree.repaint()
  }

  view.listenTo(view.tree.mouse.clicks)
  view.reactions += {
    case e: MouseClicked => {
      if((e.modifiers & Meta) == Meta) {
        val path = view.tree.getClosestPathForLocation(e.point.x, e.point.y)
        view.tree.selectPaths(path)
        nodeModel = (path.head, path.last)
        import DefinitionNode.Type._
        nodeModel._2.nodeType match {
          case Keyspace =>
          case ColumnFamily => {
            view.popup.show(view.tree, e.point.x, e.point.y)
          }
        }
      }
    }
  }

  view.tree.listenTo(view.Popup.browse)
  view.tree.reactions += {
    case ButtonClicked(view.Popup.browse) => {
      val (keyspace, cf) = nodeModel
      val queryController = new QueryController(keyspace, cf) with CassandraAware {
        override lazy val cassandraService = self.cassandraService
      }
      view.desktop += queryController.view
      queryController.view.front
    }
  }

  view.listenTo(view.newConnectionMenuItem)
  view.reactions += {
    case ButtonClicked(view.newConnectionMenuItem) => {
      new ConnectionController
    }
  }

  view.listenTo(view.refreshMenuItem)
  view.reactions += {
    case ButtonClicked(view.refreshMenuItem) => {
      refresh
    }
  }

  refresh
}
