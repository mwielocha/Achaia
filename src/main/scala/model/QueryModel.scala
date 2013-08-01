package model

import com.netflix.astyanax.model.{Column, Rows, Row}
import components.{SimpleTreeTableNode}
import scala.collection.JavaConversions._
import org.jdesktop.swingx.treetable.TreeTableNode

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 29.07.2013
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */

case class QueryModel(val size: Int)

case object EmptyQueryResult extends SimpleTreeTableNode[String] {
  def childrenStream: Stream[_ <: TreeTableNode] = Stream.Empty

  def valueAt(index: Int): String = "Empty result"

  def columnCount: Int = 2

  def parent: Option[_ <: SimpleTreeTableNode[String]] = None
}

case class QueryResultNode(val rows: Seq[QueryRowResultNode], size: Int) extends SimpleTreeTableNode[String] {

  val sequance = rows.map(row => row.copy(parent = Some(this)))

  def childrenStream = sequance.toStream

  def valueAt(index: Int): String = "Result"

  def columnCount: Int = 2

  def parent: Option[_ <: SimpleTreeTableNode[String]] = None
}

object QueryResultNode {

  def apply(rows: Rows[_, _], size: Int): QueryResultNode = {
    QueryResultNode(rows.toStream.take(size).map(QueryRowResultNode(_)), size)
  }
}

case class QueryRowResultNode(val parent: Option[QueryResultNode], val key: String, val columns: Seq[QueryColumnResultNode], size: Int = 10) extends SimpleTreeTableNode[String] {

  val sequence = columns.map(_.copy(parent = Some(this)))

  val values = Seq(key, "")

  def childrenStream = sequence.toStream

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2

}

object QueryRowResultNode {

  def apply(row: Row[_, _]): QueryRowResultNode = {
    QueryRowResultNode(None, row.getKey.toString, row.getColumns.toStream.map(QueryColumnResultNode(_)))
  }
}

case class QueryColumnResultNode(val parent: Option[QueryRowResultNode], val name: String, val value: String) extends SimpleTreeTableNode[String] {

  val values = Seq(name, value)

  def childrenStream: Stream[_ <: TreeTableNode] = Stream.empty[TreeTableNode]

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2
}

object QueryColumnResultNode {

  def apply(column: Column[_]): QueryColumnResultNode = {
    QueryColumnResultNode(None, column.getName.toString, column.getStringValue)
  }
}
