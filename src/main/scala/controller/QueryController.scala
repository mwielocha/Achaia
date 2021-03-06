package controller

import _root_.util.{StringUtils, ExceptionHandling, JsonFormatter, Logging}
import view.QueryView
import model._
import cassandra.CassandraAware
import scala.collection.JavaConversions._
import javax.swing.table.AbstractTableModel
import com.google.common.collect.TreeBasedTable
import scala.swing.event.{ValueChanged, EditDone, TableRowsSelected, ButtonClicked}
import org.jdesktop.swingx.treetable.{TreeTableNode, DefaultTreeTableModel}
import javax.swing.tree.TreeNode
import java.util
import components.TreeSelectionChanged
import akka.actor._
import com.netflix.astyanax.model.Rows
import com.netflix.astyanax.model.Column
import async.Async._
import akka.pattern._
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.json.{JSONFormat, JSON}
import com.google.gson.{JsonParser, JsonObject, GsonBuilder, Gson}
import scala.swing.Swing
import com.google.common.base.Strings
import scala.concurrent.Future
import scala.swing.event.ButtonClicked
import scala.Some
import components.TreeSelectionChanged

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 29.07.2013
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
class QueryController(val keyspace: DefinitionNode, val cf: DefinitionNode) extends Logging with ExceptionHandling {

  self: CassandraAware =>

  implicit val timeout = Timeout(12, TimeUnit.HOURS)

  val view = new QueryView(keyspace.name + "/" + cf.name)

  val queryingActor = system.actorOf(Props(new QueryingActor))

  def setQueryResult(queryResult: TreeTableNode) = {
    Swing.onEDT {
      view.treeTable.model = new DefaultTreeTableModel(queryResult, Seq("Key", "Value"))
      view.treeTable.adjustColumn(0)
      view.progressBar.indeterminate = false
    }
  }

  def performQuery(query: Unit => Rows[_, _]) = {
    view.progressBar.indeterminate = true
    Future {
      withExceptionHandling({
        setQueryResult(QueryResultNode(query(), 20))
      })
    }
  }

  view.listenTo(view.treeTable.selection)
  view.reactions += {
    case e: TreeSelectionChanged => {
      e.path.getLastPathComponent match {
        case node: QueryColumnResultNode => {
          // TODO: clean
          Future {
            val json = JsonFormatter.format(node.valueAt(1))
            Swing.onEDT {
              view.editor.text = json
            }
            node.parent match {
              case Some(parent) => {
                Swing.onEDT {
                  view.rowKeyTextField.text = parent.valueAt(0)
                  view.rowKeyTextField.repaint
                  view.rowKeyTextField.revalidate
                }
              }
              case _ =>
            }
          }
        }
        case _ =>
      }
    }
  }

  view.listenTo(view.Query.rowKeyField)
  view.reactions += {
    case ValueChanged(view.Query.rowKeyField) => {
      import StringUtils._
      view.Query.clmKeyField.enabled = !isNullOrEmpty(view.Query.rowKeyField.text)
    }
  }

  view.listenTo(view.Query.button)
  view.reactions += {
    case ButtonClicked(view.Query.button) => {
      import StringUtils._
      view.Query.rowKeyField.text match {
        case rowKey if(isNullOrEmpty(rowKey)) => {
          performQuery(Unit => {
            cassandraService.query(keyspace.name, cf.name)
          })
        }
        case rowKey => {
          view.Query.clmKeyField.text match {
            case clmKey if(isNullOrEmpty(clmKey) || !view.Query.clmKeyField.enabled) => {
              performQuery(Unit => {
                cassandraService.queryWithRowKey(keyspace.name, cf.name, rowKey)
              })
            }
            case clmKey => {
              performQuery(Unit => {
                cassandraService.queryWithBothKeys(keyspace.name, cf.name, rowKey, clmKey)
              })
            }
          }
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


