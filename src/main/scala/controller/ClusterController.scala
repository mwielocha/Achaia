package controller

import view.ClusterView
import cassandra.CassandraAware
import model.DefinitionNode
import scalaswingcontrib.tree.TreeModel
import scala.swing.event.{ButtonClicked, Key, MouseClicked}
import Key.Modifier._
import java.awt.Point

/**
 * author mikwie
 *
 */
class ClusterController {

  self: CassandraAware =>

  val view = new ClusterView(cassandraService.cassandraUri)

  var corner = 0

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

  def openQueryView(keyspace: DefinitionNode, columnFamily: DefinitionNode) = {
    val queryController = new QueryController(keyspace, columnFamily) with CassandraAware {
      override lazy val cassandraService = self.cassandraService
    }
    corner = corner match {
      case conrner if(conrner > 100) => 0
      case _ => corner + 25
    }
    queryController.view.location = new Point(corner, corner)
    view.desktop += queryController.view
    queryController.view.front
  }

  view.tree.listenTo(view.Popup.browse)
  view.tree.reactions += {
    case ButtonClicked(view.Popup.browse) => {
      val (keyspace, cf) = nodeModel
       openQueryView(keyspace, cf)
    }
  }

  view.listenTo(view.tree.mouse.clicks)
  view.reactions += {
    case MouseClicked(view.tree, point, _, 2, _) => {
      val path = view.tree.getClosestPathForLocation(point.x, point.y)
      path.last match {
        case columnFamily @ DefinitionNode(_, DefinitionNode.Type.ColumnFamily, _)  => {
          path.head match {
            case keyspace @ DefinitionNode(_, DefinitionNode.Type.Keyspace, _) => {
              openQueryView(keyspace, columnFamily)
            }
          }
        }
        case _ =>
      }
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
