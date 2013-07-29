package view

import moreswing.swing.InternalFrame
import scala.swing.{ScrollPane, Table, BorderPanel}
import java.awt._
import javax.swing._
import javax.swing.table.TableCellRenderer
import model.{ColumnModel, RowModel}
import scala.collection.JavaConversions._
import model.ColumnModel
import model.RowModel
import javax.swing.event.ListDataListener
import model.RowModel

/**
 * author mikwie
 *
 */
class QueryView(title: String) extends InternalFrame(title, true, true, true, true) {

  val table = new Table() {
    override lazy val peer: JTable = new JTable() {
      override def getCellRenderer(row: Int, column: Int): TableCellRenderer = {
        new TableCellRenderer {
          def getTableCellRendererComponent(table: JTable, value: Any, p3: Boolean, p4: Boolean, p5: Int, p6: Int): Component = {
            value match {
              case m: RowModel => {
                column match {
                  case 0 => {
                    new JLabel(m.key)
                  }
                  case 1 => {
                    new JList(new ListModel[String] {
                      def getElementAt(index: Int): String = m.columns(index).value

                      def getSize: Int = m.columns.size

                      def addListDataListener(p1: ListDataListener) {}

                      def removeListDataListener(p1: ListDataListener) {}
                    })
                  }
                }
              }
              case _ => new JLabel("Error")
            }
          }
        }
      }
    }
  }
  table.showGrid = true
  table.autoResizeMode = Table.AutoResizeMode.AllColumns

  val leftPanel = new BorderPanel

  contents = new BorderPanel {
    add(leftPanel, BorderPanel.Position.West)
    add(new ScrollPane(table), BorderPanel.Position.Center)
  }

  bounds = new Rectangle(10, 10, 500, 500)

  visible = true


}
