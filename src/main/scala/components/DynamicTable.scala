package components

import scala.swing.{Component, Table}
import java.awt.Color
import tools.TableColumnAdjuster

/**
 * author mikwie
 *
 */
class DynamicTable extends Table {

  private val tableAdjuster = new TableColumnAdjuster(peer)

  def adjustColumns: DynamicTable = {
    tableAdjuster.adjustColumns()
    this
  }

  def adjustColumn(index: Int): DynamicTable = {
    tableAdjuster.adjustColumn(index)
    this
  }
}
