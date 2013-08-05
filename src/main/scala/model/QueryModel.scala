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

trait AbstractQueryResultNode extends SimpleTreeTableNode[String] {

  def withParent(parent: AbstractQueryResultNode): AbstractQueryResultNode

}

case object EmptyQueryResult extends AbstractQueryResultNode {

  def withParent(parent: AbstractQueryResultNode): AbstractQueryResultNode = this

  def childrenStream: Stream[_ <: TreeTableNode] = Stream.Empty

  def valueAt(index: Int): String = "Empty result"

  def columnCount: Int = 2

  def parent: Option[_ <: SimpleTreeTableNode[String]] = None
}

case class QueryResultNode(val rows: Seq[QueryRowResultNode], size: Int) extends AbstractQueryResultNode {

  val sequance = rows.map(row => row.copy(parent = Some(this)))

  def withParent(parent: AbstractQueryResultNode): QueryResultNode = this

  def childrenStream = sequance.toStream

  def valueAt(index: Int): String = "Result"

  def columnCount: Int = 2

  def parent: Option[_ <: SimpleTreeTableNode[String]] = None
}

object QueryResultNode {

  def apply(rows: Rows[_, _], size: Int): QueryResultNode = {
    QueryResultNode(rows.toStream.take(size).map(QueryRowResultNode(_, 60)), size)
  }
}

case class QueryRowResultNode(val parent: Option[AbstractQueryResultNode], val key: String, val columns: Seq[AbstractQueryResultNode], size: Int = 10) extends AbstractQueryResultNode {

  val sequence = columns.map(_.withParent(this))

  val values = Seq(key, "")


  def withParent(parent: AbstractQueryResultNode): AbstractQueryResultNode = copy(parent = Some(parent))

  def childrenStream = sequence.toStream

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2

}

object QueryRowResultNode {

  def apply(row: Row[_, _], size: Int): QueryRowResultNode = {
    row.getColumns.toStream.map(QueryColumnResultNode(_)) match {
      case columns if(columns.size > size) => QueryRowResultNode(None, row.getKey.toString, columns.take(size) :+ QueryMoreResultNode(None))
      case columns => QueryRowResultNode(None, row.getKey.toString, columns.take(size))
    }
  }
}

case class QueryColumnResultNode(val parent: Option[AbstractQueryResultNode], val name: String, val value: String) extends AbstractQueryResultNode {

  val values = Seq(name, value)

  def withParent(parent: AbstractQueryResultNode): AbstractQueryResultNode = copy(parent = Some(parent))

  def childrenStream: Stream[_ <: TreeTableNode] = Stream.empty[TreeTableNode]

  def valueAt(index: Int): String = values(index)

  def columnCount: Int = 2
}

object QueryColumnResultNode {

  def apply(column: Column[_]): QueryColumnResultNode = {
    QueryColumnResultNode(None, column.getName.toString, column.getStringValue)
  }
}

case class QueryMoreResultNode(val parent: Option[AbstractQueryResultNode]) extends AbstractQueryResultNode {

  def withParent(parent: AbstractQueryResultNode): AbstractQueryResultNode = copy(parent = Some(parent))

  def childrenStream: Stream[_ <: TreeTableNode] = Stream.Empty

  def valueAt(index: Int): String = ""

  def columnCount: Int = 2

}

