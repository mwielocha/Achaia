package controller

import view.QueryView
import model.{ColumnModel, RowModel, Node}
import cassandra.CassandraAware
import scala.collection.JavaConversions._
import javax.swing.table.AbstractTableModel
import com.google.common.collect.TreeBasedTable

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

  val tableData: Seq[RowModel] = cassandraService.query(keyspace.name, cf.name).toStream.take(100).map(row => {
    RowModel(row.getKey, row.getColumns.toStream.take(6).map(column => {
      ColumnModel(column.getName, column.getStringValue)
    }))
  })

  val tableModel = new AbstractTableModel {
    def getRowCount: Int = tableData.size

    def getColumnCount: Int = 2

    def getValueAt(row: Int, column: Int): AnyRef = tableData(row)

    override def getColumnName(index: Int): String = {
      Seq("Row", "Columns")(index)
    }
  }

  view.table.model = tableModel
  tableModel.fireTableDataChanged()
}
