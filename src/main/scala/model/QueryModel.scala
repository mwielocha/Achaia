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

case class QueryResultNode(val rows: Rows[_, _], size: Int) extends SimpleTreeTableNode[String] {

  def childrenStream = rows.toStream.take(size).map(new QueryRowResultNode(Some(this), _)).force

  def valueAt(index: Int): String = "Result"

  def columnCount: Int = 2

  def parent: Option[_ <: SimpleTreeTableNode[String]] = None
}

class QueryRowResultNode(val parent: Option[QueryResultNode], val row: Row[_, _], size: Int = 10) extends SimpleTreeTableNode[String] {

  val values = Seq(row.getKey.toString, "")

  def childrenStream = row.getColumns.toStream.take(size).map(new QueryColumnResultNode(Some(this), _)).force

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2

}

class QueryColumnResultNode(val parent: Option[QueryRowResultNode], val column: Column[_]) extends SimpleTreeTableNode[String] {

  val values = Seq(column.getName.toString, column.getStringValue)

  def childrenStream: Stream[_ <: TreeTableNode] = Stream.empty[TreeTableNode]

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2
}
