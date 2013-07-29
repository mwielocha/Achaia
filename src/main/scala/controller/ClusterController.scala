package controller

import view.ClusterView
import cassandra.CassandraAware
import model.Node
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

  var nodeModel: (Node, Node) = _

  def refresh = {
    val treeModel: Seq[Node] = cassandraService.getKeyspaces.sorted.map(k => {
      import Node.Type._
      Node(k, Keyspace, cassandraService.getColumnFamilies(k).sorted.map(Node(_, ColumnFamily)))
    })

    view.tree.model = TreeModel[Node](treeModel: _*)(_.children)
    view.tree.expandAll
    view.tree.repaint()
  }

  view.listenTo(view.tree.mouse.clicks)
  view.reactions += {
    case e: MouseClicked => {
      if((e.modifiers & Meta) == Meta) {
        val path = view.tree.getClosestPathForLocation(e.point.x, e.point.y)
        nodeModel = (path.head, path.last)
        import Node.Type._
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

  refresh
}
