package model

import com.netflix.astyanax.model.{ColumnList, Row, Rows}
import java.util
import java.util.Collections
import javax.swing.table.AbstractTableModel
import scala.collection.JavaConversions._

/**
 * author mikwie
 *
 */
class BrowseModel[K, C](val rows: Rows[K, C])

object BrowseModel {

  def empty[K, C] = new BrowseModel[K, C](new Rows[K, C] {

    def getKeys: util.Collection[K] = Collections.emptyList()

    def getRow(key: K): Row[K, C] = null

    def getRowByIndex(i: Int): Row[K, C] = null

    def size(): Int = 0

    def isEmpty: Boolean = true

    def iterator(): util.Iterator[Row[K, C]] = Collections.emptyList().iterator()
  })
}

class BrowseTableModel[C](val columns: ColumnList[C]) extends AbstractTableModel {

  val maxValueLength = 160

  val stream = columns.toStream


  override def getColumnName(p1: Int): String = Seq("Column name", "Column value")(p1)

  def getRowCount: Int = columns.size()

  def getColumnCount: Int = 2

  def getValueAt(row: Int, column: Int): String = {
    val cl = stream(row)
    column match {
      case 0 => cl.getName.toString
      case 1 => {
        val all = cl.getStringValue
        if(all.length > maxValueLength) {
          all.take(maxValueLength) + "..."
        } else {
          all
        }
      }
      case 2 => ""
    }
  }
}
