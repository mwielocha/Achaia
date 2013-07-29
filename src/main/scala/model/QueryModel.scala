package model

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 29.07.2013
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
case class RowModel(key: String, columns: Seq[ColumnModel] = Nil)

case class ColumnModel(val name: String, val value: String)

case class QueryModel(val size: Int)
