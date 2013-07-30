package controller

import view.QueryView
import model.{BrowseModel, ColumnModel, RowModel, Node}
import cassandra.CassandraAware
import scala.collection.JavaConversions._
import javax.swing.table.AbstractTableModel
import com.google.common.collect.TreeBasedTable
import scala.swing.event.{TableRowsSelected, ButtonClicked}

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 29.07.2013
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
class QueryController(val keyspace: Node, val cf: Node) {

  self: CassandraAware =>

  val view = new QueryView(keyspace.name + "/" + cf.name)

  view.browseView.model = new BrowseModel[String, AnyRef](
    cassandraService.query(keyspace.name, cf.name)
  )

  view.listenTo(view.Query.button)
  view.reactions += {
    case ButtonClicked(view.Query.button) => {
      val rowKey: String = view.Query.field.text
      view.browseView.model = new BrowseModel[String, AnyRef](
        rowKey match {
          case rowKey if(rowKey == null || rowKey.isEmpty) => cassandraService.query(keyspace.name, cf.name)
          case rowKey => cassandraService.queryWithRowKey(keyspace.name, cf.name, rowKey)
        }
      )
    }
  }

  view.browseView.selections.foreach(view.listenTo(_))
  view.reactions += {
    case e: TableRowsSelected => {
      val row = e.range.head
      val value = e.source.model.getValueAt(row, 1).asInstanceOf[String]
      view.editor.text = value
    }
  }
}
