package controller

import _root_.util.{JsonFormatter, Logging}
import view.QueryView
import model._
import cassandra.CassandraAware
import scala.collection.JavaConversions._
import javax.swing.table.AbstractTableModel
import com.google.common.collect.TreeBasedTable
import scala.swing.event.{TableRowsSelected, ButtonClicked}
import org.jdesktop.swingx.treetable.{TreeTableNode, DefaultTreeTableModel}
import javax.swing.tree.TreeNode
import java.util
import components.TreeSelectionChanged
import akka.actor._
import com.netflix.astyanax.model.Rows
import async.Async._
import akka.pattern._
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.json.{JSONFormat, JSON}
import com.google.gson.{JsonParser, JsonObject, GsonBuilder, Gson}
import scala.swing.Swing
import com.google.common.base.Strings

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 29.07.2013
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
class QueryController(val keyspace: DefinitionNode, val cf: DefinitionNode) extends Logging {

  self: CassandraAware =>

  implicit val timeout = Timeout(12, TimeUnit.HOURS)

  val view = new QueryView(keyspace.name + "/" + cf.name)

  val queryingActor = system.actorOf(Props(new QueryingActor))

  def setQueryResult(queryResult: TreeTableNode) = {
    view.treeTable.model = new DefaultTreeTableModel(queryResult, Seq("Key", "Value"))
    view.treeTable.adjustColumn(0)
  }

  def performQuery(q: Unit => Rows[_, _]) = {
    view.progressBar.indeterminate = true
    (queryingActor ? QueryRequest(q)).mapTo[Rows[_, _]].map(rows => {
      setQueryResult(QueryResultNode(rows, 20))
      Swing.onEDT({
        view.progressBar.indeterminate = false
      })
    })
  }

  view.listenTo(view.treeTable.selection)
  view.reactions += {
    case e: TreeSelectionChanged => {
      e.path.getLastPathComponent match {
        case node: QueryColumnResultNode => {
          view.editor.text = JsonFormatter.format(node.valueAt(1))
          node.parent match {
            case Some(parent) => {
              view.rowKeyTextField.text = parent.valueAt(0)
              view.rowKeyTextField.repaint
              view.rowKeyTextField.revalidate
            }
            case _ =>
          }
        }
        case _ =>
      }
    }
  }

  view.listenTo(view.Query.button)
  view.reactions += {
    case ButtonClicked(view.Query.button) => {
      view.Query.field.text match {
        case key if(key == null || key.isEmpty) => {
          performQuery(Unit => {
            cassandraService.query(keyspace.name, cf.name)
          })
        }
        case key => {
          performQuery(Unit => {
            cassandraService.queryWithRowKey(keyspace.name, cf.name, key)
          })
        }
      }
    }
  }

  setQueryResult(EmptyQueryResult)

  performQuery(Unit => {
    cassandraService.query(keyspace.name, cf.name)
  })

  case class QueryRequest(q: Unit => Rows[_, _])

  class QueryingActor extends Actor {

    def receive = {
      case QueryRequest(query) => {
        val rows: Rows[_, _] = query()
        sender ! rows
      }
    }
  }
}


